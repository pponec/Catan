package Catan;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/*
 * Note - you must include the url type -- either "http://" or
 * "file://".
 */
public class BrowserControl
{
    /**
     * Display a file in the system browser.  If you want to display a
     * file, you must include the absolute path name.
     *
     * @param url the file's url (the url must start with either "http://" or
     * "file://").
     */
    public static void displayURL(String url)
    {
        try
        {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE))
            {
                Desktop.getDesktop().browse(new URI(url));
                return;
            }
        }
        catch (IOException | URISyntaxException | UnsupportedOperationException x)
        {
            showUrlFallback(url, x);
            return;
        }

        launchBrowserFallback(url);
    }

    private static void launchBrowserFallback(String url)
    {
        boolean windows = isWindowsPlatform();
        String cmd = null;
        try
        {
            if (windows)
            {
                cmd = WIN_PATH + " " + WIN_FLAG + " " + url;
                new ProcessBuilder(WIN_PATH, WIN_FLAG, url).start();
            }
            else
            {
                cmd = UNIX_PATH + " -remote openURL(" + url + ")";
                Process p = new ProcessBuilder(UNIX_PATH, "-remote", "openURL(" + url + ")").start();
                try
                {
                    int exitCode = p.waitFor();
                    if (exitCode != 0)
                    {
                        cmd = UNIX_PATH + " " + url;
                        new ProcessBuilder(UNIX_PATH, url).start();
                    }
                }
                catch (InterruptedException x)
                {
                    Thread.currentThread().interrupt();
                    showUrlFallback(url, x);
                    System.err.println("Error bringing up browser, cmd='" + cmd + "'");
                    System.err.println("Caught: " + x);
                }
            }
        }
        catch (IOException x)
        {
            showUrlFallback(url, x);
            System.err.println("Could not invoke browser, command=" + cmd);
            System.err.println("Caught: " + x);
        }
    }

    private static void showUrlFallback(String url, Exception x)
    {
        MessageJDialog e = new MessageJDialog(null, true, false);
        e.setText(url, true);
        e.setVisible(true);
        if (x != null)
        {
            System.err.println("Could not open browser for url=" + url);
            System.err.println("Caught: " + x);
        }
    }

    /**
     * Try to determine whether this application is running under Windows
     * or some other platform by examing the "os.name" property.
     *
     * @return true if this application is running under a Windows OS
     */
    public static boolean isWindowsPlatform()
    {
        String os = System.getProperty("os.name");
        return os != null && os.startsWith(WIN_ID);
    }

    /**
     * Simple example.
     */
    public static void main(String[] args)
    {
        displayURL("http://www.javaworld.com");
    }

    // Used to identify the windows platform.
    private static final String WIN_ID = "Windows";
    // The default system browser under windows.
    private static final String WIN_PATH = "rundll32";
    // The flag to display a url.
    private static final String WIN_FLAG = "url.dll,FileProtocolHandler";
    // The default browser under unix.
    private static final String UNIX_PATH = "netscape";
    // The flag to display a url.
    private static final String UNIX_FLAG = "-remote openURL";
}
