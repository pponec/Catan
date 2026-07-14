package Catan;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests for the longest-road length calculation in {@link Player}.
 *
 * <p>They exercise the two fixes:
 * <ol>
 *   <li>a road must be broken by a foreign building sitting on a vertex along it
 *       (the {@code roadLen} DFS now tests the vertex it extends <em>through</em>);</li>
 *   <li>a road component with no empty "tip" segment - a closed loop, or a run
 *       boxed in by foreign roads - is still measured (the {@code calcRoadLens}
 *       fallback), instead of scoring 0.</li>
 * </ol>
 *
 * <p>{@link Player} has a heavy Swing constructor, so the tests use its bare
 * package-private constructor, which only runs the field initialisers. The road
 * logic touches {@code gameRules} only through {@code Player.Debug}, which swallows
 * the resulting NPE, so the calculation completes normally.
 */
class RoadLengthTest
{
    /** A fresh player whose only initialised state is an empty road list. */
    private Player newPlayer()
    {
        return new Player();
    }

    private BuildPoint point()
    {
        return new BuildPoint(null);
    }

    /** Create an edge between two points and wire the adjacency both ways. */
    private Road edge(BuildPoint a, BuildPoint b, Player owner)
    {
        Road r = new Road(null);
        r.owner = owner;
        r.buildJoins.add(a);
        r.buildJoins.add(b);
        a.addRoadJoin(r);
        b.addRoadJoin(r);
        return r;
    }

    /** Add an empty (unbuilt) "tip" segment so the tip search has a start point. */
    private void openEnd(BuildPoint at)
    {
        edge(at, point(), null);
    }

    // ----- fix 1: a foreign building breaks the road -------------------------

    @Test
    void straightRoadOfThreeCountsThree()
    {
        Player me = newPlayer();
        BuildPoint p0 = point(), p1 = point(), p2 = point(), p3 = point();
        Road r1 = edge(p0, p1, me);
        Road r2 = edge(p1, p2, me);
        Road r3 = edge(p2, p3, me);
        openEnd(p0);
        openEnd(p3);
        me.builtRoadObjs.add(r1);
        me.builtRoadObjs.add(r2);
        me.builtRoadObjs.add(r3);

        assertEquals(3, me.calcRoadLens(null));
    }

    @Test
    void foreignBuildingInTheMiddleBreaksTheRoad()
    {
        Player me = newPlayer();
        Player opp = new Player();
        BuildPoint p0 = point(), p1 = point(), p2 = point(), p3 = point();
        Road r1 = edge(p0, p1, me);
        Road r2 = edge(p1, p2, me);
        Road r3 = edge(p2, p3, me);
        p1.owner = opp;            // opponent settlement splits the route at p1
        openEnd(p0);
        openEnd(p3);
        me.builtRoadObjs.add(r1);
        me.builtRoadObjs.add(r2);
        me.builtRoadObjs.add(r3);

        // longest continuous run is r2+r3 = 2, NOT the whole 3
        assertEquals(2, me.calcRoadLens(null));
    }

    @Test
    void ownBuildingInTheMiddleDoesNotBreakTheRoad()
    {
        Player me = newPlayer();
        BuildPoint p0 = point(), p1 = point(), p2 = point(), p3 = point();
        Road r1 = edge(p0, p1, me);
        Road r2 = edge(p1, p2, me);
        Road r3 = edge(p2, p3, me);
        p1.owner = me;             // our own settlement does not interrupt
        openEnd(p0);
        openEnd(p3);
        me.builtRoadObjs.add(r1);
        me.builtRoadObjs.add(r2);
        me.builtRoadObjs.add(r3);

        assertEquals(3, me.calcRoadLens(null));
    }

    @Test
    void foreignBuildingAtJunctionSeparatesBothArms()
    {
        Player me = newPlayer();
        Player opp = new Player();
        BuildPoint pa = point(), pc = point(), pb = point();
        Road ra = edge(pa, pc, me);
        Road rb = edge(pc, pb, me);
        pc.owner = opp;            // opponent on the shared junction
        openEnd(pa);
        openEnd(pb);
        me.builtRoadObjs.add(ra);
        me.builtRoadObjs.add(rb);

        // the two arms are not joined through pc -> longest run is 1
        assertEquals(1, me.calcRoadLens(null));
    }

    // ----- fix 2: components without an empty tip are still measured ---------

    @Test
    void closedLoopWithoutAnyTipIsMeasured()
    {
        Player me = newPlayer();
        BuildPoint[] p = { point(), point(), point(), point(), point(), point() };
        for (int i = 0; i < 6; i++)
        {
            me.builtRoadObjs.add(edge(p[i], p[(i + 1) % 6], me));
        }
        // no open ends at all -> the tip search finds nothing

        assertEquals(6, me.calcRoadLens(null));
    }

    @Test
    void straightRunBoxedInByForeignRoadsIsMeasured()
    {
        Player me = newPlayer();
        Player opp = new Player();
        BuildPoint p0 = point(), p1 = point(), p2 = point(),
                   p3 = point(), p4 = point(), p5 = point();
        Road r1 = edge(p0, p1, me);
        Road r2 = edge(p1, p2, me);
        Road r3 = edge(p2, p3, me);
        Road r4 = edge(p3, p4, me);
        Road r5 = edge(p4, p5, me);
        // both ends blocked by foreign roads (owner != null) -> no empty tip seed
        edge(p0, point(), opp);
        edge(p5, point(), opp);

        // add in an order that starts the fallback from a middle road, to prove
        // it sums the whole run (5) rather than just one arm (3)
        me.builtRoadObjs.add(r3);
        me.builtRoadObjs.add(r1);
        me.builtRoadObjs.add(r5);
        me.builtRoadObjs.add(r2);
        me.builtRoadObjs.add(r4);

        assertEquals(5, me.calcRoadLens(null));
    }

    @Test
    void loopWithTailIsMeasuredByTheTipSearch()
    {
        Player me = newPlayer();
        BuildPoint p0 = point(), p1 = point(), p2 = point(), p3 = point();
        Road r0 = edge(p0, p1, me);
        Road r1 = edge(p1, p2, me);
        Road r2 = edge(p2, p0, me);   // triangle
        Road r3 = edge(p0, p3, me);   // tail off the loop
        openEnd(p3);                  // tail end has a tip
        me.builtRoadObjs.add(r0);
        me.builtRoadObjs.add(r1);
        me.builtRoadObjs.add(r2);
        me.builtRoadObjs.add(r3);

        // tail (1) + once around the triangle (3) = 4
        assertEquals(4, me.calcRoadLens(null));
    }

    // ----- loops: a closed road counts every segment, once ------------------
    //
    // The longest road is the longest run that never reuses a segment; a vertex
    // may be passed through more than once, so a closed loop contributes all of
    // its edges. These cases mirror the bug report where BLUE's ring around a
    // tile plus a tail was the true longest road yet the bonus went elsewhere.

    /** Wire a closed loop through the given points (last joins back to first). */
    private void loop(Player owner, BuildPoint... pts)
    {
        for (int i = 0; i < pts.length; i++)
        {
            owner.builtRoadObjs.add(edge(pts[i], pts[(i + 1) % pts.length], owner));
        }
    }

    /**
     * BLUE's shape from the screenshot: a six-segment ring with a three-segment
     * tail. The longest road enters the tail, reaches the ring and goes the whole
     * way round: tail (3) + loop (6) = 9. This is the length that must out-rank a
     * rival's merely branched network.
     */
    @Test
    void hexLoopWithTailOfThreeCountsNine()
    {
        Player me = newPlayer();
        BuildPoint[] h = { point(), point(), point(), point(), point(), point() };
        loop(me, h);                                   // hexagonal ring, 6 segments

        BuildPoint t1 = point(), t2 = point(), t3 = point();
        me.builtRoadObjs.add(edge(h[0], t1, me));
        me.builtRoadObjs.add(edge(t1, t2, me));
        me.builtRoadObjs.add(edge(t2, t3, me));
        openEnd(t3);

        assertEquals(9, me.calcRoadLens(null));
    }

    /**
     * A single ring carrying a tail on two opposite corners. The best run is one
     * tail plus the entire ring (2 + 6 = 8); it cannot then reach the second tail
     * without reusing a ring segment, so the two tails never both count.
     */
    @Test
    void hexLoopWithTwoOppositeTailsCountsEight()
    {
        Player me = newPlayer();
        BuildPoint[] h = { point(), point(), point(), point(), point(), point() };
        loop(me, h);

        BuildPoint a1 = point(), a2 = point();
        me.builtRoadObjs.add(edge(h[0], a1, me));
        me.builtRoadObjs.add(edge(a1, a2, me));
        openEnd(a2);

        BuildPoint b1 = point(), b2 = point();
        me.builtRoadObjs.add(edge(h[3], b1, me));
        me.builtRoadObjs.add(edge(b1, b2, me));
        openEnd(b2);

        assertEquals(8, me.calcRoadLens(null));
    }

    /**
     * Two loops sharing a single vertex (a figure eight) and no open tip at all.
     * One edge-distinct trail threads both loops through the shared vertex, so
     * all six segments count.
     */
    @Test
    void figureEightSharingOneVertexCountsSix()
    {
        Player me = newPlayer();
        BuildPoint c = point();
        loop(me, c, point(), point());   // loop A through c
        loop(me, c, point(), point());   // loop B through c

        assertEquals(6, me.calcRoadLens(null));
    }

    /**
     * Two triangular loops joined by a single bridge segment (a dumbbell), again
     * with no open tip. Once around the first loop (3) + the bridge (1) + once
     * around the second (3) = 7.
     */
    @Test
    void twoLoopsJoinedByABridgeCountSeven()
    {
        Player me = newPlayer();
        BuildPoint a0 = point(), b0 = point();
        loop(me, a0, point(), point());          // loop A anchored at a0
        me.builtRoadObjs.add(edge(a0, b0, me));  // bridge
        loop(me, b0, point(), point());          // loop B anchored at b0

        assertEquals(7, me.calcRoadLens(null));
    }

    /**
     * A theta: three separate paths between the same two junctions (two loops
     * that share both endpoints). Both junctions have odd degree, so an Euler
     * trail walks every one of the five segments.
     */
    @Test
    void thetaWithThreePathsBetweenTwoNodesCountsFive()
    {
        Player me = newPlayer();
        BuildPoint u = point(), v = point(), w = point(), x = point();
        me.builtRoadObjs.add(edge(u, v, me));   // direct path
        me.builtRoadObjs.add(edge(u, w, me));   // path via w
        me.builtRoadObjs.add(edge(w, v, me));
        me.builtRoadObjs.add(edge(u, x, me));   // path via x
        me.builtRoadObjs.add(edge(x, v, me));

        assertEquals(5, me.calcRoadLens(null));
    }
}
