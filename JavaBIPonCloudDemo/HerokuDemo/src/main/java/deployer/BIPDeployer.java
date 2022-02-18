package deployer;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.BasicConfigurator;
import org.javabip.api.BIPActor;
import org.javabip.api.BIPEngine;
import org.javabip.api.BIPGlue;
import org.javabip.engine.factory.EngineFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import akka.actor.ActorSystem;
import deployer.buildpack.HerokuBuildpack;
import deployer.herokuAddons.AddOnAvailability;
import deployer.herokuAddons.database.*;
import deployer.herokuAddons.monitoring.*;
import deployer.herokuContainer.HerokuDynoType;
import deployer.herokuContainer.HerokuRegion;


//@WebServlet(name = "Main Server", urlPatterns = "/main")
public class BIPDeployer extends HttpServlet{
	 /**
	 */
	private static final long serialVersionUID = 1L;
	
	// Authorization Token is used for API and CLI  
	public static String authorizationToken = "ddb26dbb-bd5a-4ccc-8247-7bc9c0f77abd";

	
	// BIP Engine Declaration
	ActorSystem system = ActorSystem.create("MySystem");
	EngineFactory engineFactory = new EngineFactory(system);
	

	BIPGlue bipGlue = new herokuBIPGlue().getBipGlue();
	
	BIPEngine engine;
	BIPDeployer BIPDeployer;
	
	// BIP Components
	Deployer deployerComponent = new Deployer();
	HerokuDynoType dynoComponent = new HerokuDynoType();
	HerokuRegion regionComponent = new HerokuRegion();
	HerokuBuildpack buildpackComponent = new HerokuBuildpack();
	//Add-on Categories
	HerokuDatabase DatabaseAddonCategoryComponent;
	HerokuMonitoring MonitoringAddonCategoryComponent;
	//Database Addons
	HerokuPostgres herokuPostgresAddonComponent = new HerokuPostgres();
	HerokuClearDBMySQL herokuClearDBMySQLAddonComponent = new HerokuClearDBMySQL();
//	HerokuJawsDBMySQL herokuJawsDBMySQLAddonComponent = new HerokuJawsDBMySQL();
	//Database Addons
	HerokuNewRelicAPM herokuNewRelicAPMAddonComponent = new HerokuNewRelicAPM();
	HerokuScoutAPM herokuScoutAPMAddonComponent = new HerokuScoutAPM();
	
	// BIP Actors
	BIPActor deployerActor;
	BIPActor dynoActor;
	BIPActor regionActor;
	BIPActor buildpackActor;
	//Addon Categories
	BIPActor DatabaseAddonCategoryActor;
	BIPActor MonitoringAddonCategoryActor;
	//Database Addons
	BIPActor herokuPostgresAddonActor;
	BIPActor herokuClearDBMySQLAddonActor;
//	BIPActor herokuJawsDBMySQLAddonActor;
	//Database Addons
	BIPActor herokuNewRelicAPMAddonActor;
	BIPActor herokuScoutAPMAddonActor;
	
	

	Configuration conf;
//	= new Configuration(1,1,1);
	Map<String, Object> confMap = new HashMap<String, Object>();

	
	public static AddOnAvailability herokuPostgresAvailability = new AddOnAvailability(false);
	Map<String, Object> herokuPostgresAvailabilityMap = new HashMap<String, Object>();
	
	public static AddOnAvailability ClearDBMySQLAvailability = new AddOnAvailability(false);
	Map<String, Object> ClearDBMySQLAvailabilityMap = new HashMap<String, Object>();
	
	public static AddOnAvailability ScoutAPMAvailability = new AddOnAvailability(false);
	Map<String, Object> ScoutAPMAvailabilityMap = new HashMap<String, Object>();
	
	public static AddOnAvailability NewRelicAPMAvailability = new AddOnAvailability(false);
	Map<String, Object> NewRelicAPMAvailabilityMap = new HashMap<String, Object>();
	

	

	// Use terminate method instead but it does not work!
	// @After
	public void cleanup() {
		system.terminate();
		System.out.println("[Java BIP] The engine is terminated!");
	}
	
	public void stop() throws ServletException {
		engine.stop();
		System.out.println("[Java BIP] The engine stopped!");
		BIPDeployer.engineFactory.destroy(engine);
		System.out.println("[Java BIP] The engine is detroyed!");
	}
	
	public void restart() throws ServletException {
		stop();
		init();
	}
	
	public void start() throws ServletException {
		init();
	}

	public void init() throws ServletException {
		// Set path for each type of OS
		HerokuCli.setOSPath();
		
		BasicConfigurator.configure();
//		log.info("This is Logger Info");

		System.out.println("---------- JavaBIP for Cloud Initializing ----------");

		BIPDeployer = new BIPDeployer();
		engine = BIPDeployer.engineFactory.create("myEngine", bipGlue);
		//
		deployerActor = engine.register(deployerComponent, "Deployer", true);
		dynoActor = engine.register(dynoComponent, "HerokuDynoType", true);
		regionActor = engine.register(regionComponent, "HerokuRegion", true);
		buildpackActor = engine.register(buildpackComponent, "HerokuBuildpack", true);
		//Database Add-ons
		herokuPostgresAddonActor = engine.register(herokuPostgresAddonComponent, "HerokuPostgres", true);
		herokuClearDBMySQLAddonActor = engine.register(herokuClearDBMySQLAddonComponent, "HerokuClearDBMySQL", true);
//		herokuJawsDBMySQLAddonActor = engine.register(herokuJawsDBMySQLAddonComponent, "HerokuJawsDBMySQL", true);
		//Monitoring Add-ons
		herokuNewRelicAPMAddonActor = engine.register(herokuNewRelicAPMAddonComponent, "HerokuNewRelicAPM", true);
		herokuScoutAPMAddonActor = engine.register(herokuScoutAPMAddonComponent, "HerokuScoutAPM", true);
		//
		System.out.println("[Java BIP] Receiving a request, the engine starts the test.....");
		engine.start();
		System.out.println("[Java BIP] The engine Execute the test.....");
		engine.execute();
		//
		herokuPostgresAvailabilityMap.put("availability", herokuPostgresAvailability);
		herokuPostgresAddonActor.inform("init", herokuPostgresAvailabilityMap);
		
		ClearDBMySQLAvailabilityMap.put("availability", ClearDBMySQLAvailability);
		herokuClearDBMySQLAddonActor.inform("init", ClearDBMySQLAvailabilityMap);
		
		ScoutAPMAvailabilityMap.put("availability", ScoutAPMAvailability);
		herokuScoutAPMAddonActor.inform("init", ScoutAPMAvailabilityMap);
		
		NewRelicAPMAvailabilityMap.put("availability", NewRelicAPMAvailability);
		herokuNewRelicAPMAddonActor.inform("init", NewRelicAPMAvailabilityMap);

//  	try {
//			TimeUnit.SECONDS.sleep(10);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		engine.stop();
//		bipMoni.engineFactory.destroy(engine);
//		bipMoni.cleanup(); 	

		System.out.println("---------- JavaBIP Initialized successfully ----------");
	}
	
	
	
	HttpServletRequest request; 
	HttpServletResponse response;
	List<String> parameterNames;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
//		doPost(request, response);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		setRequest(request, response);
		

////			String[] postgresql = {"heroku-postgresql", "hobby-basic"};
//			Addons.add(postgresql);
////			String[] cleardb = {"cleardb", "ignite"};
////			Addons.add(cleardb);
////			String[] jawsdb = {"jawsdb", "kitefirn"};
////			Addons.add(jawsdb);
////			String[] scout = {"scout", "chair"};
////			Addons.add(scout);
////			String[] newrelic = {"newrelic", "wayne"};
////			Addons.add(newrelic);

//		}
		// This APIs will be modified asap, these are quite messy
		if (!parameterNames.isEmpty()) {
			if (checkParam(0, "req", "start")) {
				System.out.println("[BIPDeployer]: Start");
				start();
				printSimpleJSON("JavaBIP starts");
			//
			} else if (checkParam(0, "req", "stop")) {
				System.out.println("[BIPDeployer]: Stop");
				stop();
				printSimpleJSON("JavaBIP stop");
			//
			} else if (checkParam(0, "req", "restart")) {
				System.out.println("[BIPDeployer]: Restart");
				restart();
				printSimpleJSON("JavaBIP restarts");
			//
			} else if (checkParam(0, "req", "detach")) {
				System.out.println("[BIPDeployer]: Container detached");
				deployerActor.inform("javaBIPdetach");
				printSimpleJSON("Container detached");
			//
			} else if (checkParam(0, "req", "deleteContainer")) {
				System.out.println("[BIPDeployer]: Container deleted");
				deployerActor.inform("deleteContainerSpontaneous");
				printSimpleJSON("Container deleted");
			//
			} else if (checkParam(0, "req", "deploy") && checkParam(1, "region", "us") && checkParam(2, "buildpack", "jvm") && checkParam(3, "addon", "heroku-postgresql") && checkParam(4, "pushApp", "true")) {
				// BIPDeployer?req=deploy&region=us&buildpack=jvm&addon=heroku-postgresql
				ArrayList<String[]> Addons = new ArrayList<String[]>();
				String[] postgresql = {"heroku-postgresql", "hobby-dev"};
				Addons.add(postgresql);
				conf = new Configuration("us","heroku/jvm",Addons);
				confMap.put("config", conf);
				deployerActor.inform("chooseConfig", confMap);
				
				waitResult();
			//
			} else if (checkParam(0, "req", "deploy") && checkParam(1, "region", "eu") && checkParam(2, "buildpack", "jvm") && checkParam(3, "addon", "heroku-postgresql") && checkParam(4, "pushApp", "true")) {
				// BIPDeployer?req=deploy&region=eu&buildpack=jvm&addon=heroku-postgresql
				ArrayList<String[]> Addons = new ArrayList<String[]>();
				String[] postgresql = {"heroku-postgresql", "hobby-dev"};
				Addons.add(postgresql);
				conf = new Configuration("eu","heroku/jvm",Addons);
				confMap.put("config", conf);
				deployerActor.inform("chooseConfig", confMap);
				
				waitResult();
			//
			} else if (checkParam(0, "req", "deploy") && checkParam(1, "region", "us") && checkParam(2, "buildpack", "jvm") && checkParam(3, "addon", "heroku-postgresql") && checkParam(4, "pushApp", "false")) {
				// BIPDeployer?req=deploy&region=eu&buildpack=jvm&addon=heroku-postgresql&pushApp=skip

				ArrayList<String[]> Addons = new ArrayList<String[]>();
				String[] postgresql = {"heroku-postgresql", "hobby-dev"};
				Addons.add(postgresql);
				conf = new Configuration("us","heroku/jvm",Addons);
				conf.pushAppSkip = true;
				confMap.put("config", conf);
				deployerActor.inform("chooseConfig", confMap);
				
				waitResult();
			//
			} else if (checkParam(0, "req", "deploy") && checkParam(1, "region", "eu") && checkParam(2, "buildpack", "jvm") && checkParam(3, "addon", "heroku-postgresql") && checkParam(4, "pushApp", "false")) {
				// BIPDeployer?req=deploy&region=eu&buildpack=jvm&addon=heroku-postgresql&pushApp=skip
				
				ArrayList<String[]> Addons = new ArrayList<String[]>();
				String[] postgresql = {"heroku-postgresql", "hobby-dev"};
				Addons.add(postgresql);
				conf = new Configuration("eu","heroku/jvm",Addons);
				conf.pushAppSkip = true;
				confMap.put("config", conf);
				deployerActor.inform("chooseConfig", confMap);
				
				waitResult();
			//
			} else if (checkParam(0, "req", "deploy") && checkParam(1, "region", "us") && checkParam(2, "buildpack", "scala") && checkParam(3, "addon", "heroku-postgresql") && checkParam(4, "pushApp", "false")) {
				// BIPDeployer?req=deploy&region=eu&buildpack=jvm&addon=heroku-postgresql
				ArrayList<String[]> Addons = new ArrayList<String[]>();
				String[] postgresql = {"heroku-postgresql", "hobby-dev"};
				Addons.add(postgresql);
				conf = new Configuration("eu","heroku/scala",Addons);
				confMap.put("config", conf);
				deployerActor.inform("chooseConfig", confMap);
			//
			} else if (checkParam(0, "req", "deploy") && checkParam(1, "region", "us") && checkParam(2, "buildpack", "scala") && checkParam(3, "addon", "heroku-postgresql") && checkParam(4, "pushApp", "false")) {
				// BIPDeployer?req=deploy&region=eu&buildpack=jvm&addon=heroku-postgresql
				ArrayList<String[]> Addons = new ArrayList<String[]>();
				String[] postgresql = {"heroku-postgresql", "hobby-dev"};
				Addons.add(postgresql);
				conf = new Configuration("eu","heroku/scala",Addons);
				confMap.put("config", conf);
				deployerActor.inform("chooseConfig", confMap);
			//
			} else {
				System.out.println("[BIPDeployer]: This kind of request is currently not supported!");
				printSimpleJSON("We are sorry for this inconvenience. Because of lack of coffee, this kind of request is currently not supported!");
			}
		} else {
			response.sendRedirect("index.jsp");
		}
		
	};	
	
	// Waiting result synchronously
	void waitResult () throws IOException {
		boolean exit = false;
		int index = 0;
		
		while (!exit) {
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			System.out.println(deployerActor.getState());
			int size = conf.getDeployStatus().size();
			if (size - 1 > index) {
				
				for (int i = index; i <= size - 1; i++) {
					
					String str = conf.getDeployStatus().get(i);
					if (str.startsWith("Error")) {
						exit = true;
						printSimpleJSON(str);
						
					}
					if (str.equals("App ready")) {
						exit = true;
						JsonObject Info = new JsonObject();
						Info.addProperty("OwnerEmail", conf.getOwnerEmail());
						Info.addProperty("AppName", conf.getAppName());
						Info.addProperty("RegionName", conf.getRegionName());
						Info.addProperty("WebUrl", conf.getWebUrl());
						
						String JsonString = new Gson().toJson(Info);
						PrintWriter out = response.getWriter();
						response.setContentType("application/json");
						response.setCharacterEncoding("UTF-8");
						out.print(JsonString);
						out.flush();
					}
				}
				index = size - 1;
			}
		}
	}
	
	
	private void printSimpleJSON (String msg) throws IOException {
		JsonObject JSONObj = new JsonObject();
		JSONObj.addProperty("Msg", msg);
		String reqJsonString = new Gson().toJson(JSONObj);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.print(reqJsonString);
		out.flush();
	}
	
	String getValueAt(int position) {
		return request.getParameter(parameterNames.get(position));
	}
	
	boolean checkParam(int position, String name, String value) {
		if (position < parameterNames.size() && name.equals(parameterNames.get(position)) && value.equals(request.getParameter(parameterNames.get(position)))) {
			return true;
		} else {
			System.err.println("Param checked failed!");
			return false;
		}
	}
	
	//This help writing additional methods faster - no need to declare arguments
	void setRequest (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.request = request;
		this.response = response;
		parameterNames = new ArrayList<String>(request.getParameterMap().keySet());
		if (parameterNames.isEmpty()) {
			System.err.println("No parameter request!");
		}
	}
			
}
