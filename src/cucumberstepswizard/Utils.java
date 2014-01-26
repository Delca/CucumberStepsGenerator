package cucumberstepswizard;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

import cucumberstepswizard.preferences.PreferenceConstants;

public class Utils {

    public static String getDisplayVersion(String stringToDisplay, String ellipsis, int maximumDisplayLength) {
        String result = stringToDisplay;

        if (ellipsis.length() < maximumDisplayLength) {

            if (result.length() > maximumDisplayLength) {
                result = result.substring(0, maximumDisplayLength - ellipsis.length()).concat(ellipsis);
            }

        }

        return result;
    }

    public static String getDisplayVersion(String stringToDisplay) {
        return getDisplayVersion(stringToDisplay, "...", 38);
    }

    public static String createList(String[] items) {
        StringBuffer buffer = new StringBuffer("");

        for (String s : items) {
            buffer.append(s);
            buffer.append(PreferenceConstants.LIST_SEPARATOR);
        }

        return buffer.toString();
    }

    public static String[] parseString(String string) {
        return string.split(PreferenceConstants.LIST_SEPARATOR);
    }

    public static boolean testForExtension(String container, String extension) {
        int dotLoc = container.lastIndexOf('.');
        if (dotLoc != -1) {
            String ext = container.substring(dotLoc + 1);
            return ext.equalsIgnoreCase(extension);
        }

        return false;
    }

 // This method will retrieve the file path corresponding to the default files
    // and packages set in the preferences
    public static String[] getDefaultPathsfromPreferences() {
        String list = cucumberstepswizard.Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.PATHS_PREFERENCE);
        if (list.length() > PreferenceConstants.LIST_SEPARATOR.length()) {
            list = list.substring(0, list.length() - PreferenceConstants.LIST_SEPARATOR.length());
        }
        Set<String> pathsToExplore = new HashSet<String>();
        Set<String> pathsToExploreNext = new HashSet<String>();
        Set<String> exploredPaths = new HashSet<String>();
        for (String path : Utils.parseString(list)) {
            pathsToExplore.add(path);
        }

        Set<String> actualStepsFilePaths = new HashSet<String>();

        do {
        for (String path : pathsToExplore) {
            // Mark resource as visited
            exploredPaths.add(path);
            // Open ressource
            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            IResource resource = root.findMember(new Path(path));

            if (resource == null) {
                log("COULD NOT FIND: " + path);

                continue;
            }

            log("CHECKING RESOURCE: " + resource.getLocation().toString());

            if (resource.getType() == IResource.FILE) {
                // It's a file -> add it to the set if it is a .java file
                String resourcePath = resource.getLocation().toString();
                if (Utils.testForExtension(resourcePath, "java")) {
                    log("FOUND FILE: " + resource.getLocation().toString());
                    actualStepsFilePaths.add(resource.getLocation().toString());
                }
            }
            else if (resource.getType() == IResource.FOLDER) {
                // It's a directory -> put the not-yet-visited paths it contains into the pathToExploreNext Set
                log("FOUND FOLDER: " + resource.getLocation().toString());
                try {
                    for (IResource member : ((IFolder) resource).members()) {
                        String newPath = member.getFullPath().toString();
                        if (!exploredPaths.contains(newPath)) {
                            log("ADDING PATH: " + member.getLocation().toString());
                            pathsToExploreNext.add(newPath);
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // Other -> duh ? Ignore that stuff.
        }

        pathsToExplore = pathsToExploreNext;
        pathsToExploreNext = new HashSet<String>();
        } while (pathsToExplore.size() > 0);

        return actualStepsFilePaths.toArray(new String[0]);
    }

    public static void log(String message) {
        log(message, null);
    }

    public static void log(String message, Exception e) {
        Activator.getDefault().getLog().log(new Status(Status.INFO, Activator.PLUGIN_ID, Status.OK, message, e));
    }

}
