import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Main {

// 1. Launcher app finds current version on disk and launches.
// 2. In background check for newer version and download newer version to disk
// 3. Create/Update version.txt, point to newest version.
// 4. Cleanup old versions and delete

    private static String timeInMillisLocalAppBase = "/Users/todddeland/IdeaProjects/TimeInMillisUpdater/prod/";
    private static String timeInMillisLocalVersionFile = timeInMillisLocalAppBase + "version.txt";
    private static String timeInMillisJarFile = "TimeInMillis.jar";

    private static String timeInMillisRemoteBase = "https://gitlab.com/mynameistodd/TimeInMillis/-/jobs/artifacts/master/raw/";
    private static String timeInMillisRemoteVersionFile = timeInMillisRemoteBase + "src/version.txt?job=build_jar";
    private static String timeInMillisRemoteJar = timeInMillisRemoteBase + "out/artifacts/TimeInMillis_jar/TimeInMillis.jar?job=build_jar";

    public static void main(String[] args) {
        System.out.println("Program Start");

        int currentVersion = findCurrentVersionOnDisk();

        Thread currentVersionThread = new Thread(() -> {
            System.out.println("Launching Current Version: " + currentVersion);
            launchCurrentVersion(currentVersion);
        });
        currentVersionThread.start();

        Thread updateThread = new Thread(() -> {
            System.out.println("Checking for updates...");
            int newVersion = checkForUpdateAndDownload(currentVersion);

            if (newVersion > currentVersion) {
                if (updateVersionConfig(newVersion)) {
                    deleteOldVersions(newVersion);
                }
            }
        });
        updateThread.start();
    }

    private static int findCurrentVersionOnDisk() {

        String version = "-1";
        try {
            version = Files.readString(Paths.get(timeInMillisLocalVersionFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Integer.parseInt(version);
    }

    private static void launchCurrentVersion(int version) {
        try {
            Process process = Runtime.getRuntime().exec("java -jar " + timeInMillisLocalAppBase + version + "/" + timeInMillisJarFile);

            InputStream inputStream = process.getInputStream();
            InputStream errorStream = process.getErrorStream();

            System.out.println("InputStream: ");
            System.out.println(new String(inputStream.readAllBytes()));

            System.out.println("ErrorStream: ");
            System.out.println(new String(errorStream.readAllBytes()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int checkForUpdateAndDownload(int currentVersion) {
        int newVersion = currentVersion; //set equal, if anything fails below then no update will perform

        try {
            URL remoteVersionUrl = new URL(timeInMillisRemoteVersionFile);
            InputStream versionStream = remoteVersionUrl.openStream();
            newVersion = Integer.parseInt(new String(versionStream.readAllBytes()));

            if (newVersion > currentVersion) {
                System.out.println("New Version Available: " + newVersion);
                Files.createDirectory(Paths.get(timeInMillisLocalAppBase + newVersion + "/"));

                URL remoteJarUrl = new URL(timeInMillisRemoteJar);
                InputStream jarStream = remoteJarUrl.openStream();

                String newVersionJarLocation = timeInMillisLocalAppBase + newVersion + "/" + timeInMillisJarFile;
                Files.write(Paths.get(newVersionJarLocation), jarStream.readAllBytes(), new StandardOpenOption[]{StandardOpenOption.CREATE});
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return newVersion;
    }

    private static boolean updateVersionConfig(int newVersion) {
        boolean success = true;
        try {
            File versionFile = new File(timeInMillisLocalVersionFile);
            FileWriter fileWriter = new FileWriter(versionFile, false);
            fileWriter.write(String.valueOf(newVersion));
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    private static void deleteOldVersions(int newVersion) {

    }
}
