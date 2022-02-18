package deployer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class HerokuCli {
	
	///////////////////////////////////////////////////////////////////////CLI Setup//////////////////////////////////////////////////////////////////
	// Cli directory
	public static String cliDir;
	// On Windows
	public static String cliDirWindows = "C:\\\\Program Files\\\\heroku\\\\bin\\\\heroku.cmd";
	// On Linux
	public static String cliDirLinux = "heroku";
	
	// War repository 
	public static String warName = "HerokuComputeDemo.war";
	public static String warPath;
	// On Windows
	public static String warRepoWindows = "D:";
	// On Linux
	public static String warRepoLinux = "/home/royal/Desktop";
	
	public static void setOSPath () {
		String os = getOsName();
		System.out.println("JavaBIP is runing on " + os);
		if (isWindows()) {
			// This is the path to your installed Heroku
			cliDir = cliDirWindows;
			// This is the path to your war file
			warPath = warRepoWindows + "\\\\" + warName;
		} else {
			// This is the path to your installed Heroku
			cliDir = cliDirLinux;
			// This is the path to your war file
			warPath = warRepoLinux + "/" + warName;
		}
	}
	///////////////////////////////////////////////////////////////////////CLI Setup - End//////////////////////////////////////////////////////////////////
		
	
	
	public static String createAddon (String addon, String plan, String appName) {
		String command = cliDir + " addons:create " + addon + ":" + plan + " -a " + appName;
		return exeCm(command);
	}
	
	
	static String warDeploy (String dir, String appName) {
		String command = cliDir + " war:deploy " + dir + " -a " + appName;
		return exeCm(command);
	}
	
	
	static String exeCm (String command) {
		String content = "";
		try {
		    Process process = Runtime.getRuntime().exec(command);
		 
		    BufferedReader reader = new BufferedReader(
		            new InputStreamReader(process.getInputStream()));
		    String line;
		    while ((line = reader.readLine()) != null) {
		    	System.out.println(line);
		        content = content + "\n" + line;
		    }
		    reader.close();
		    
		    BufferedReader errorReader = new BufferedReader(
		            new InputStreamReader(process.getErrorStream()));
		    while ((line = errorReader.readLine()) != null) {
		        System.out.println(line);
		        System.out.println(line);
		        content = content + "\n" + line;
		    }
		    errorReader.close();
		 
		} catch (IOException e) {
		    e.printStackTrace();
		}
		return content;
	}
	
	
	
	/////////////////////////////////////////////////////////////////////// OS detection //////////////////////////////////////////////////////////////////
	private static String OS = null;
	
	public static String getOsName()
	{
		if(OS == null) { 
			OS = System.getProperty("os.name"); 
			}
		return OS;
	}
	
	public static boolean isWindows()
	{
		return getOsName().startsWith("Windows");
	}
	
	public static boolean isUnix()
	{
		return getOsName().startsWith("Linux");
	}
	/////////////////////////////////////////////////////////////////////// OS detection - End //////////////////////////////////////////////////////////////////

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// Use for testing
//		HerokuCli h = new HerokuCli();
//		System.out.println("Hello!!!");
//		h.warDeploy(dir, appName);
		//h.runCm("heroku login --interactive");
		//h.interactiveLogin();
		
//		String test = "Created postgresql-symmetrical-01970 as DATABASE_URL";
//		String database_url = StringUtils.substringBetween(test, "Created", "as DATABASE_URL");
//		System.out.println(database_url);
		
//		String command = "heroku war:deploy src/war/HerokuComputeDemo.war -a shrouded-springs-80174 ";
		
	
		
	}

}
