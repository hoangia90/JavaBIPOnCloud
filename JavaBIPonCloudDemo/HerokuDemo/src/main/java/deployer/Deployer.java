package deployer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.javabip.annotations.ComponentType;
import org.javabip.annotations.Data;
import org.javabip.annotations.Guard;
import org.javabip.annotations.Port;
import org.javabip.annotations.Ports;
import org.javabip.annotations.Transition;
import org.javabip.annotations.Transitions;
import org.javabip.api.PortType;

@Ports({
	@Port(name = "chooseConfig", type = PortType.spontaneous),
	//
	@Port(name = "setFreeDyno", type = PortType.enforceable),
	//
	@Port(name = "setUSRegion", type = PortType.enforceable),
	@Port(name = "setEURegion", type = PortType.enforceable),
	//
	@Port(name = "setAddonsForUS", type = PortType.enforceable), 
	@Port(name = "setAddonsForEU", type = PortType.enforceable), 
	//
	@Port(name = "setJava", type = PortType.enforceable),
	@Port(name = "setScala", type = PortType.enforceable),
	@Port(name = "setPython", type = PortType.enforceable),
	@Port(name = "setRuby", type = PortType.enforceable),
	@Port(name = "setNodejs", type = PortType.enforceable),
	@Port(name = "setClojure", type = PortType.enforceable),
	@Port(name = "setGradle", type = PortType.enforceable),
	@Port(name = "setJvm", type = PortType.enforceable),
	@Port(name = "setPhp", type = PortType.enforceable),
	@Port(name = "setGo", type = PortType.enforceable),
	//
	@Port(name = "setAddonsForJava", type = PortType.enforceable),
	@Port(name = "setAddonsForScala", type = PortType.enforceable),
	@Port(name = "setAddonsForPython", type = PortType.enforceable),
	@Port(name = "setAddonsForRuby", type = PortType.enforceable),
	@Port(name = "setAddonsForNodejs", type = PortType.enforceable),
	@Port(name = "setAddonsForClojure", type = PortType.enforceable),
	@Port(name = "setAddonsForGradle", type = PortType.enforceable),
	@Port(name = "setAddonsForJvm", type = PortType.enforceable),
	@Port(name = "setAddonsForPhp", type = PortType.enforceable),
	@Port(name = "setAddonsForGo", type = PortType.enforceable),
	//
	@Port(name = "addHerokuPostgres1", type = PortType.enforceable),
	@Port(name = "addHerokuPostgres2", type = PortType.enforceable),
	@Port(name = "addClearDBMySQL1", type = PortType.enforceable),
	@Port(name = "addScoutAPM1", type = PortType.enforceable),
	@Port(name = "addNewRelicAPM1", type = PortType.enforceable),
	@Port(name = "receiveAddonResponse", type = PortType.enforceable),
	//
	@Port(name = "addonAddingError", type = PortType.enforceable),
	@Port(name = "dynoCreatingError", type = PortType.enforceable),
	@Port(name = "receiveDynoResponse", type = PortType.enforceable),
	@Port(name = "deleteContainer", type = PortType.enforceable),
	@Port(name = "deleteContainerSpontaneous", type = PortType.spontaneous),
	//
	@Port(name = "pushAppToContainer", type = PortType.enforceable),
	@Port(name = "finish", type = PortType.enforceable),
	@Port(name = "appPushingError", type = PortType.enforceable),
	//
	@Port(name = "resetAll", type = PortType.enforceable),
	@Port(name = "javaBIPdetach", type = PortType.spontaneous),
})

@ComponentType(initial = "Init", name = "deployer.Deployer")
public class Deployer  {
	
	Configuration conf;
	private ArrayList<String[]> Addons;
//	private ArrayList<String[]> Addons = new ArrayList<String[]>();
	//
	@Transitions({
	@Transition(name = "chooseConfig", source = "Init", target = "CofigurationChosen"),
	})
	public void chooseConfig(@Data(name = "config") Configuration conf) {	
		// Init conf
		this.conf = conf;
		Addons = copyAddons(conf.getAddons());
//		for(String e[] : (ArrayList<String[]>) conf.getAddons()) {
//			Addons.add(e);
//		}
		System.out.println("[HerokuDeployer] ChooseConfig triggered .... (Init --> CofigurationChosen): Buildpack(" + this.conf.getBuildpack() + "), Addon(" + this.conf.getAddons().size() +")" );
		System.out.println();
		//
		conf.getDeployStatus().add("CofigurationChosen");
	}
	
	
	//
	@Transitions({
		@Transition(name = "setFreeDyno", source = "CofigurationChosen", target = "FreeDynoSet"),
	})
	public void setFreeDyno() throws IOException {	
		System.out.println("[HerokuDeployer] setFreeDyno triggered .... (CofigurationChosen --> FreeDynoSet)");
		System.out.println();
		//
		conf.getDeployStatus().add("FreeDynoSet - Waiting for response" );
	}
	@Guard(name = "isRegionNull")
	public boolean isRegionNull () throws IOException {
		if (conf.getRegionName().isEmpty() || conf.getRegionName().isEmpty()) {
			return true;
		} else {
			return false;
		}
	}
	// Adding more options for deploying
	@Data(name = "DynoOptions")
	public ArrayList<String> DynoOptions() {
		ArrayList<String> DynoOptions = new ArrayList<String>();
		DynoOptions.add(conf.getAppName());
		DynoOptions.add(conf.getRegionName());
		DynoOptions.add(conf.getStackName());
		return DynoOptions;
	}
	
	
	//
	Response DynoResponseData;
	////////Data
	@Transitions({
		@Transition(name = "receiveDynoResponse", source = "FreeDynoSet", target = "FreeDynoSet", guard = "isDynoResponseDataNull"),
	})
	public void receiveDynoResponse(@Data(name = "DynoResponseData") Response DynoResponseData) throws IOException {	
		System.out.println("[HerokuDeployer] receiveDynoResponse triggered .... (FreeDynoSet --> FreeDynoSet)");
		this.DynoResponseData = DynoResponseData;
		System.out.println("[HerokuDeployer] receiveDynoResponse receive Response Code(" + DynoResponseData.responseCode +"); Content: " + DynoResponseData.JSONContent);
		// Set Dyno infomation into Conf
		conf.setFreeDynoInfoJson(DynoResponseData.JSONContent);
		System.out.println();
		//
		conf.getDeployStatus().add("FreeDynoSet (Received Dyno Component Response) - Container name: " + conf.getAppName() + ", address " + conf.getWebUrl() );
	}
	@Guard(name = "isDynoResponseDataNull")
	public boolean isDynoResponseDataNull () throws IOException {
		if (DynoResponseData == null ) {
			return true;
		} else {
			return false;
		}
	}
	
	
	//
	@Transitions({
		@Transition(name = "dynoCreatingError", source = "FreeDynoSet", target = "Error", guard = "isDynoCreatingError"),
	})
	public void dynoCreatingError() throws IOException {	
		System.out.println("[HerokuDeployer] dynoCreatingError triggered .... (FreeDynoSet --> Error)");
		System.out.println("[HerokuDeployer] Error: " + DynoResponseData.responseCode + " " + DynoResponseData.JSONContent);
		System.out.println();
		//
		conf.getDeployStatus().add("Error (Dyno Creating Error) - Content: " + DynoResponseData.JSONContent );
		// Reset var DynoResponseData
		DynoResponseData = null;
	}
	@Guard(name = "isDynoCreatingError")
	public boolean isDynoCreatingError () throws IOException {
		if (DynoResponseData.getResponseCode() > 300 || DynoResponseData == null) {
			return true;
		} else {
			return false;
		}
	}
	
	
	//
	@Transitions({
		@Transition(name = "setUSRegion", source = "FreeDynoSet", target = "US", guard = "isSetUSRegion"),
	})
	public void setUSRegion() {	
		System.out.println("[HerokuDeployer] setUSRegion triggered .... (FreeDynoSet --> US)");
		System.out.println();
		// Reset var DynoResponseData
		DynoResponseData = null;
		//
		conf.getDeployStatus().add("Container hosted at US" );
	}
	@Guard(name = "isSetUSRegion")
	public boolean isSetUSRegion () throws IOException {
		System.out.println(conf.getRegionName());
		if (DynoResponseData != null && !conf.getAppName().isEmpty() && conf.getRegionName().equals("us") && DynoResponseData.getResponseCode() <= 300) {
			return true;
		} else {
			return false;
		}
	}
	//
	@Transitions({
		@Transition(name = "setEURegion", source = "FreeDynoSet", target = "EU", guard = "isSetEURegion"),
	})
	public void setEURegion() {	
		System.out.println("[HerokuDeployer] setEURegion triggered .... (FreeDynoSet --> EU)");
		System.out.println();
		// Reset var DynoResponseData
		DynoResponseData = null;
		//
		conf.getDeployStatus().add("Container hosted at EU" );
	}
	@Guard(name = "isSetEURegion")
	public boolean isSetEURegion () throws IOException {
		System.out.println(conf.getRegionName());
		if (DynoResponseData != null && !conf.getAppName().isEmpty() && conf.getRegionName().equals("eu") && DynoResponseData.getResponseCode() <= 300) {
			return true;
		} else {
			return false;
		}
	}
	
	
	//
	@Transitions({
		@Transition(name = "setAddonsForUS", source = "US", target = "ContainerCreated"),
		})
		public void setAddonsForUS() {	
			System.out.println("[HerokuDeployer] setAddonsForUS triggered .... (US --> ContainerCreated)");
			System.out.println();
			//
			conf.getDeployStatus().add("Set available add-ons for the container at US" );
	}
	
	
	//
	@Transitions({
		@Transition(name = "setAddonsForEU", source = "EU", target = "ContainerCreated"),
		})
		public void setAddonsForEU() {	
			System.out.println("[HerokuDeployer] setAddonsForEU triggered .... (EU --> ContainerCreated)");
			System.out.println();
			//
			conf.getDeployStatus().add("Set available add-ons for the container at EU" );
	}
	
	
	// Used for adding builpacks and add-ons to an app/container
	@Data(name = "AppName")
	public String sendAppName() {
		return conf.getAppName();
	}
	
	//
	@Transitions({
		@Transition(name = "setJava", source = "ContainerCreated", target = "Java", guard = "isSetJava"),
		})
		public void setJava() {	
			System.out.println("[HerokuDeployer] setJava triggered .... (ContainerCreated --> Java)");
			System.out.println();
			
			conf.getDeployStatus().add("Set builpack : Java" );
	}
	@Guard(name = "isSetJava")
	public boolean isSetJava() throws IOException {
		if (conf.getBuildpack() == "heroku/java") {
			return true;
		} else {
			return false;
		}
	}
	
	//
	@Transitions({
		@Transition(name = "setJvm", source = "ContainerCreated", target = "Jvm", guard = "isSetJvm"),
		})
		public void setJvm() {	
			System.out.println("[HerokuDeployer] setJvm triggered .... (ContainerCreated --> Jvm)");
			System.out.println();
			
			conf.getDeployStatus().add("Set builpack : Jvm" );
	}
	@Guard(name = "isSetJvm")
	public boolean isSetJvm() throws IOException {
		if (conf.getBuildpack() == "heroku/jvm") {
			return true;
		} else {
			return false;
		}
	}
	
	//
	@Transitions({
		@Transition(name = "setScala", source = "ContainerCreated", target = "Scala", guard = "isSetScala"),
		})
		public void setScala() {	
			System.out.println("[HerokuDeployer] setScala triggered .... (ContainerCreated --> Scala)");
			System.out.println();
			
			conf.getDeployStatus().add("Set builpack : Scala" );
	}
	@Guard(name = "isSetScala")
	public boolean isSetScala() throws IOException {
		if (conf.getBuildpack() == "heroku/scala") {
			return true;
		} else {
			return false;
		}
	}
	
	//
	@Transitions({
		@Transition(name = "setPython", source = "ContainerCreated", target = "Python", guard = "isSetPython"),
		})
		public void setPython() {	
			System.out.println("[HerokuDeployer] setPython triggered .... (ContainerCreated --> Python)");	
			System.out.println();
			
			conf.getDeployStatus().add("Set builpack : Python" );
	}
	@Guard(name = "isSetPython")
	public boolean isSetPython() throws IOException {
		if (conf.getBuildpack() == "heroku/python") {
			return true;
		} else {
			return false;
		}
	}
	
	//
	@Transitions({
		@Transition(name = "setRuby", source = "ContainerCreated", target = "Ruby", guard = "isSetRuby"),
		})
		public void setRuby() {	
			System.out.println("[HerokuDeployer] setRuby triggered .... (ContainerCreated --> Ruby)");
			System.out.println();
			
			conf.getDeployStatus().add("Set builpack : Ruby" );
	}
	@Guard(name = "isSetRuby")
	public boolean isSetRuby() throws IOException {
		if (conf.getBuildpack() == "heroku/ruby") {
			return true;
		} else {
			return false;
		}
	}
	
	//
	@Transitions({
		@Transition(name = "setNodejs", source = "ContainerCreated", target = "Nodejs", guard = "isSetNodejs"),
		})
		public void setNodejs() {	
			System.out.println("[HerokuDeployer] setNodejs triggered .... (ContainerCreated --> Nodejs)");
			System.out.println();
			
			conf.getDeployStatus().add("Set builpack : Nodejs" );
	}
	@Guard(name = "isSetNodejs")
	public boolean isSetNodejs() throws IOException {
		if (conf.getBuildpack() == "heroku/nodejs") {
			return true;
		} else {
			return false;
		}
	}
	
	//
	@Transitions({
		@Transition(name = "setClojure", source = "ContainerCreated", target = "Clojure", guard = "isSetClojure"),
		})
		public void setClojure() {	
			System.out.println("[HerokuDeployer] setClojure triggered .... (ContainerCreated --> Clojure)");
			System.out.println();
			
			conf.getDeployStatus().add("Set builpack : Clojure" );
	}
	@Guard(name = "isSetClojure")
	public boolean isSetClojure() throws IOException {
		if (conf.getBuildpack() == "heroku/clojure") {
			return true;
		} else {
			return false;
		}
	}
	
	//
	@Transitions({
		@Transition(name = "setGradle", source = "ContainerCreated", target = "Gradle", guard = "isSetGradle"),
		})
		public void setGradle() {	
			System.out.println("[HerokuDeployer] setGradle triggered .... (ContainerCreated --> Gradle)");
			System.out.println();
			
			conf.getDeployStatus().add("Set builpack : Gradle" );
	}
	@Guard(name = "isSetGradle")
	public boolean isSetGradle() throws IOException {
		if (conf.getBuildpack() == "heroku/gradle") {
			return true;
		} else {
			return false;
		}
	}
	
	//
	@Transitions({
		@Transition(name = "setPhp", source = "ContainerCreated", target = "Php", guard = "isSetPhp"),
		})
		public void setPhp() {	
			System.out.println("[HerokuDeployer] setPhp triggered .... (ContainerCreated --> Php)");
			System.out.println();
			
			conf.getDeployStatus().add("Set builpack : Php" );
	}
	@Guard(name = "isSetPhp")
	public boolean isSetPhp() throws IOException {
		if (conf.getBuildpack() == "heroku/php") {
			return true;
		} else {
			return false;
		}
	}
	
	//
	@Transitions({
		@Transition(name = "setGo", source = "ContainerCreated", target = "Go", guard = "isSetGo"),
		})
		public void setGo() {	
			System.out.println("[HerokuDeployer] setGo triggered .... (ContainerCreated --> Go)");
			System.out.println();
			
			conf.getDeployStatus().add("Set builpack : Go" );
	}
	@Guard(name = "isSetGo")
	public boolean isSetGo() throws IOException {
		if (conf.getBuildpack() == "heroku/go") {
			return true;
		} else {
			return false;
		}
	}
	
	
	
	////////
	@Transitions({
		@Transition(name = "setAddonsForJava", source = "Java", target = "BuildpackSet"),
		})
		public void setAddonsForJava() {	
			System.out.println("[HerokuDeployer] setAddonsForJava triggered .... (Java --> BuildpackSet)");
			System.out.println();
			
			conf.getDeployStatus().add("Set setAddons for Java");
	}
	
	@Transitions({
		@Transition(name = "setAddonsForJvm", source = "Jvm", target = "BuildpackSet"),
		})
		public void setAddonsForJvm() {	
			System.out.println("[HerokuDeployer] setAddonsForJvm triggered .... (Jvm --> BuildpackSet)");
			System.out.println();
			
			conf.getDeployStatus().add("Set setAddons for Jvm");
	}
	
	@Transitions({
		@Transition(name = "setAddonsForScala", source = "Scala", target = "BuildpackSet"),
		})
		public void setAddonsForScala() {	
			System.out.println("[HerokuDeployer] setAddonsForScala triggered .... (Scala --> BuildpackSet)");
			System.out.println();
			
			conf.getDeployStatus().add("Set setAddons for Scala");
	}
	
	@Transitions({
		@Transition(name = "setAddonsForPython", source = "Python", target = "BuildpackSet"),
		})
		public void setAddonsForPython() {	
			System.out.println("[HerokuDeployer] setAddonsForPython triggered .... (Python --> BuildpackSet)");
			System.out.println();
			
			conf.getDeployStatus().add("Set setAddons for Python");
	}
	
	@Transitions({
		@Transition(name = "setAddonsForRuby", source = "Ruby", target = "BuildpackSet"),
		})
		public void setAddonsForRuby() {	
			System.out.println("[HerokuDeployer] setAddonsForRuby triggered .... (Ruby --> BuildpackSet)");
			System.out.println();
			
			conf.getDeployStatus().add("Set setAddons for Ruby");
	}
	
	@Transitions({
		@Transition(name = "setAddonsForNodejs", source = "Nodejs", target = "BuildpackSet"),
		})
		public void setAddonsForNodejs() {	
			System.out.println("[HerokuDeployer] setAddonsForNodejs triggered .... (Nodejs --> BuildpackSet)");
			System.out.println();
			
			conf.getDeployStatus().add("Set setAddons for Nodejs");
	}
	
	@Transitions({
		@Transition(name = "setAddonsForClojure", source = "Clojure", target = "BuildpackSet"),
		})
		public void setAddonsForClojure() {	
			System.out.println("[HerokuDeployer] setAddonsForClojure triggered .... (Clojure --> BuildpackSet)");
			System.out.println();
			
			conf.getDeployStatus().add("Set setAddons for Clojure");
	}
	
	@Transitions({
		@Transition(name = "setAddonsForGradle", source = "Gradle", target = "BuildpackSet"),
		})
		public void setAddonsForGradle() {	
			System.out.println("[HerokuDeployer] setAddonsForGradle triggered .... (Gradle --> BuildpackSet)");
			System.out.println();
			
			conf.getDeployStatus().add("Set setAddons for Gradle");
	}
	
	@Transitions({
		@Transition(name = "setAddonsForPhp", source = "Php", target = "BuildpackSet"),
		})
		public void setAddonsForPhp() {	
			System.out.println("[HerokuDeployer] setAddonsForPhp triggered .... (Php --> BuildpackSet)");
			System.out.println();
			
			conf.getDeployStatus().add("Set setAddons for Php");
	}
	
	@Transitions({
		@Transition(name = "setAddonsForGo", source = "Go", target = "BuildpackSet"),
		})
		public void setAddonsForGo() {	
			System.out.println("[HerokuDeployer] setAddonsForGo triggered .... (Go --> BuildpackSet)");
			System.out.println();
			
			conf.getDeployStatus().add("Set setAddons for Go");
	}
	
	
	
	
	////////Add-ons
	boolean addonErrorDetected = false;
	
	
//	Addons = ArrayList<String[]>();
	@Transitions({
		@Transition(name = "addHerokuPostgres1", source = "BuildpackSet", target = "BuildpackSet", guard = "isAddHerokuPostgres1"),
	})
	public void addHerokuPostgres1() {	
		System.out.println("[HerokuDeployer] addHerokuPostgres1 triggered .... (BuildpackSet --> BuildpackSet)");
		System.out.println();
		removeAddon("heroku-postgresql", Addons);
		
		conf.getDeployStatus().add("Add Heroku Postgres (hobby-dev)");
	}
	@Guard(name = "isAddHerokuPostgres1")
	public boolean isAddHerokuPostgres1() throws IOException {
		System.out.println("[HerokuDeployer] isAddHerokuPostgres1 guard .... HerokuPostgres availability = " + BIPDeployer.herokuPostgresAvailability.isAvailability());
		if (isInAddons("heroku-postgresql", Addons) && findPlan("heroku-postgresql", Addons).equals("hobby-dev") && BIPDeployer.herokuPostgresAvailability.isAvailability()) {
			return true;
		} else {
			return false;
		}
	}
	
	@Transitions({
		@Transition(name = "addHerokuPostgres2", source = "BuildpackSet", target = "BuildpackSet", guard = "isAddHerokuPostgres2"),
	})
	public void addHerokuPostgres2() {	
		System.out.println("[HerokuDeployer] addHerokuPostgres2 triggered .... (BuildpackSet --> BuildpackSet)");
		System.out.println();
		removeAddon("heroku-postgresql", Addons);
		
		conf.getDeployStatus().add("Add Heroku Postgres (hobby-basic)");
	}
	@Guard(name = "isAddHerokuPostgres2")
	public boolean isAddHerokuPostgres2() throws IOException {
		System.out.println("[HerokuDeployer] isAddHerokuPostgres2 guard .... HerokuPostgres availability = " + BIPDeployer.herokuPostgresAvailability.isAvailability());
		if (isInAddons("heroku-postgresql", Addons) && findPlan("heroku-postgresql", Addons).equals("hobby-basic") && BIPDeployer.herokuPostgresAvailability.isAvailability()) {
			return true;
		} else {
			return false;
		}
	}
	
	
	@Transitions({
		@Transition(name = "addClearDBMySQL1", source = "BuildpackSet", target = "BuildpackSet", guard = "isAddClearDBMySQL1"),
		})
		public void addClearDBMySQL1() {	
			System.out.println("[HerokuDeployer] addClearDBMySQL1 triggered .... (BuildpackSet --> BuildpackSet)");
			System.out.println();
			removeAddon("cleardb", Addons);
			
			conf.getDeployStatus().add("Add ClearDB MySQL (ignite)");
	}
	@Guard(name = "isAddClearDBMySQL1")
	public boolean isAddClearDBMySQL1() throws IOException {
		System.out.println("[HerokuDeployer] isAddClearDBMySQL1 guard .... ClearDBMySQL availability = " + BIPDeployer.ClearDBMySQLAvailability.isAvailability());
		if (isInAddons("cleardb", Addons) && findPlan("cleardb", Addons).equals("ignite") && BIPDeployer.ClearDBMySQLAvailability.isAvailability()) {
			return true;
		} else {
			return false;
		}
	}
	
	

	@Transitions({
		@Transition(name = "addScoutAPM1", source = "BuildpackSet", target = "BuildpackSet", guard = "isAddScoutAPM1"),
		})
		public void addScoutAPM1() {	
			System.out.println("[HerokuDeployer] addScoutAPM1 triggered .... (BuildpackSet --> BuildpackSet)");
			System.out.println();
			removeAddon("scout", Addons);
			
			conf.getDeployStatus().add("Add Scout APM (chair)");
	}
	@Guard(name = "isAddScoutAPM1")
	public boolean isAddScoutAPM1() throws IOException {
		System.out.println("[HerokuDeployer] isAddScoutAPM1 guard .... ScoutAPM availability = " + BIPDeployer.ScoutAPMAvailability.isAvailability());
		if (isInAddons("scout", Addons) && findPlan("scout", Addons).equals("chair") && BIPDeployer.ScoutAPMAvailability.isAvailability()) {
			return true;
		} else {
			return false;
		}
	}
	
	@Transitions({
		@Transition(name = "addNewRelicAPM1", source = "BuildpackSet", target = "BuildpackSet", guard = "isAddNewRelicAPM1"),
		})
		public void addNewRelicAPM1() {	
			System.out.println("[HerokuDeployer] addNewRelicAPM1 triggered .... (BuildpackSet --> BuildpackSet)");
			System.out.println();
			removeAddon("newrelic", Addons);
			
			conf.getDeployStatus().add("Add New Relic APM (wayne)");
	}
	@Guard(name = "isAddNewRelicAPM1")
	public boolean isAddNewRelicAPM1() throws IOException {
		System.out.println("[HerokuDeployer] isAddNewRelicAPM1 guard .... NewRelicAPM availability = " + BIPDeployer.NewRelicAPMAvailability.isAvailability());
		if (isInAddons("newrelic", Addons) && findPlan("newrelic", Addons).equals("wayne") && BIPDeployer.NewRelicAPMAvailability.isAvailability()) {
			return true;
		} else {
			return false;
		}
	}
	
	String AddonResponse;
	@Transitions({
		@Transition(name = "receiveAddonResponse", source = "BuildpackSet", target = "BuildpackSet"),
		})
		public void receiveAddonResponse(@Data(name = "AddonResponse") String AddonResponse) {	
			this.AddonResponse = AddonResponse;
			System.out.println("[HerokuDeployer] receiveAddonResponse triggered .... (BuildpackSet --> BuildpackSet)");
			String database_url = StringUtils.substringBetween(AddonResponse, "Created", "as DATABASE_URL");
			System.out.println("Addon Name: " + database_url + ", Content: " + AddonResponse);
			if (database_url == null) {
				if (addonErrorDetected == false) {
					addonErrorDetected = true;
				}
			}
			System.out.println("[HerokuDeployer] receiveAddonResponse addonErrorDetected: " + addonErrorDetected);
			System.out.println();
			
			conf.getDeployStatus().add("Buildpack set (Received add-on response) - Content: " + AddonResponse);
	}
	
	
	//Adding Add-on Error
	@Transitions({
		@Transition(name = "addonAddingError", source = "BuildpackSet", target = "Error", guard = "isAddonAddingError"),
		})
		public void addonAddingError() {	
			System.out.println("[HerokuDeployer] addonAddingError triggered --> Error!!! .... (BuildpackSet --> Error)");
			System.out.println();
			
			conf.getDeployStatus().add("Error (Addon adding error) - Content: " + AddonResponse);
	}
	@Guard(name = "isAddonAddingError")
	public boolean isAddonAddingError() {
		
		boolean re = false;
		ArrayList<String[]> arr = (ArrayList<String[]>) conf.getAddons();
		
		for (String addon[] : arr) {
			if (addon[0].equals("heroku-postgresql") && !BIPDeployer.herokuPostgresAvailability.isAvailability()) {
				System.out.println("[HerokuDeployer] addonAddingError guard .... HerokuPostgres can not be added!!!");
				re = true;
				break;
			} else if ( addon[0].equals("cleardb") && !BIPDeployer.ClearDBMySQLAvailability.isAvailability()) {
				System.out.println("[HerokuDeployer] addonAddingError guard .... ClearDBMySQL can not be added!!!");
				re = true;
				break;
			} else if ( addon[0].equals("jawsdb") && !BIPDeployer.NewRelicAPMAvailability.isAvailability()) {
				System.out.println("[HerokuDeployer] addonAddingError guard .... NewRelicAPM can not be added!!!");
				re = true;
				break;
			} else if ( addon[0].equals("scout") && !BIPDeployer.ScoutAPMAvailability.isAvailability()) {
				System.out.println("[HerokuDeployer] addonAddingError guard .... ScoutAPM can not be added!!!");
				re = true;
				break;
			} else if ( addon[0].equals("newrelic") && !BIPDeployer.NewRelicAPMAvailability.isAvailability()) {
				System.out.println("[HerokuDeployer] addonAddingError guard .... NewRelic can not be added!!!");
				re = true;
				break;
			}
		}

		if (addonErrorDetected == true) {
			re = true;
		}
		System.out.println("isAddonAddingError: " + re);
		return re;
		
	}
	
	
	////////
	Boolean pushAppError = false;
	String pushResponse;
	@Transitions({
		@Transition(name = "pushAppToContainer", source = "BuildpackSet", target = "AppPushed", guard = "isAddOnsEmpty"),
		})
		public void pushAppToContainer() {	
			System.out.println("[HerokuDeployer] pushAppToContainer triggered .... (AddonAdded --> AppPushed)");
			System.out.println();
			
			if (!conf.pushAppSkip) {
				
				String pushResponse = HerokuCli.warDeploy(HerokuCli.warPath, conf.getAppName());
				
				String url = StringUtils.substringBetween(pushResponse, "remote:", "deployed to Heroku");
				if (url == null) {
					pushAppError = true;
				} else {
					System.out.println(url + HerokuCli.warName);
				}
				conf.getDeployStatus().add("App pushed - Content: " + pushResponse);
			
			} else {
				conf.getDeployStatus().add("App pushed - Skipped: ");
			}
			
			
			
	}
	@Guard(name = "isAddOnsEmpty")
	public boolean isAddOnsEmpty() throws IOException {
		System.out.println("[HerokuDeployer] pushAppToContainer guard(isAddOnsEmpty) ....  = " + addonsIsEmpty(Addons));
		System.out.println(Addons.size());
		return (addonsIsEmpty(Addons) && addonErrorDetected == false);
	}
	
	@Transitions({
		@Transition(name = "finish", source = "AppPushed", target = "AppReady", guard = "isSuccess"),
		})
		public void receiveAddonResponse() {	
			System.out.println("[HerokuDeployer] finish triggered .... (AppPushed --> AppReady)");
			System.out.println();
			
			conf.getDeployStatus().add("App ready");
	}
	@Guard(name = "isSuccess")
	public boolean isSuccess() throws IOException {
		return !pushAppError;
	}
	
	@Transitions({
		@Transition(name = "appPushingError", source = "AppPushed", target = "Error", guard = "isPushingError"),
		})
		public void appPushingError() {	
			System.out.println("[HerokuDeployer] appPushingError triggered .... (AppPushed --> Error)");
			System.out.println();
			
			conf.getDeployStatus().add("Error (App pushing error) - Content: " + pushResponse);
			// Reset var pushResponse
			pushResponse = null;
	}
	@Guard(name = "isPushingError")
	public boolean isPushingError() throws IOException {
		return pushAppError;
	}
	
	
	@Transitions({
		@Transition(name = "deleteContainer", source = "Error", target = "Deleted"),
		@Transition(name = "deleteContainerSpontaneous", source = "AppReady", target = "Deleted"),
		})
		public void deleteContainer() throws IOException {	
			System.out.println("[HerokuDeployer] deleteContainer triggered --> Current Container deleted! .... (Error --> Deleted)");
			System.out.println();
			URL obj = new URL("https://api.heroku.com/apps/" + conf.getAppName());
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("DELETE");
			//add request header
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept", "application/vnd.heroku+json; version=3");
			con.setRequestProperty("Authorization", "Bearer " + BIPDeployer.authorizationToken);
			//
			int responseCode = con.getResponseCode();
			//
			String inputLine = null;
			StringBuffer jSONContent = new StringBuffer();
			BufferedReader in;
			if (responseCode < 300) {
				 in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				//
				while ((inputLine = in.readLine()) != null) {
					jSONContent.append(inputLine);
				}
			} else {
				 in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
				//
				while ((inputLine = in.readLine()) != null) {
					jSONContent.append(inputLine);
				}
			}
			in.close();
			
			System.out.println("[HerokuDeployer] Deleting......." + responseCode + " " + jSONContent.toString());
			
			conf.getDeployStatus().add("App: " + conf.getAppName() + "is deleted");
	}
	
	@Transitions({
		@Transition(name = "resetAll", source = "Deleted", target = "Init"),
		@Transition(name = "resetAll", source = "Detached", target = "Init"),
		})
		public void resetAll() throws IOException {	
			System.out.println("[HerokuDeployer] resetAll triggered .... (Deleted --> Init)");
			setNullAllData();
			System.out.println();
			conf.getDeployStatus().add("Reset all components");
			conf.setNull();
	}
	
	
	@Transitions({
		@Transition(name = "javaBIPdetach", source = "AppReady", target = "Detached"),
		})
		public void javaBIPdetach() throws IOException {	
			System.out.println("[HerokuDeployer] javaBIPdetach triggered .... (AppReady --> Init)");
			setNullAllData();
			System.out.println();
			conf.getDeployStatus().add("JavaBIP is detached");
	}
	
	
	public void setNullAllData() {
		DynoResponseData = null;
		addonErrorDetected = false;
		AddonResponse = null;
		pushAppError = false;
		pushResponse = null;
		Addons = null;
	}
	
	
	
	// Add-on list manipulation
	boolean isInAddons(String addon, ArrayList<String[]> Addons) {
		for(String[] a : Addons){
			if(a[0].equals(addon)) {
				return true;
			}
		}
		return false;
	}
	
	void removeAddon(String addon, ArrayList<String[]> Addons) {
//		for(String[] a : Addons){
//			if(a[0].equals(addon)) {
//				Addons.remove(a);
//				Addons.
//			}
//		}
		for (int i = 0; i < Addons.size(); i++) {
			if(Addons.get(i)[0].equals(addon)) {
				Addons.remove(i);
			}
		}
	}
	
	String findPlan(String addon, ArrayList<String[]> Addons) {
		String plan = "";
		for(String[] a : Addons){
			if(a[0].equals(addon)) {
				plan = a[1].toString();
			}
		}
		return plan;
	}
	
	ArrayList<String[]> copyAddons(ArrayList<String[]> Addons) {
		ArrayList<String[]> l = new ArrayList<String[]>();
		for(String e[] : Addons){
			l.add(e);
		}
		return l;
	}
	
	boolean addonsIsEmpty(ArrayList<String[]> Addons) {
		return Addons.isEmpty(); 
	}

}
