package Tree;

import java.util.Arrays;

public class HerokuTree {
		
		public HerokuTree() {
			DataStoresInit();
			MonitoringInit();
		}

		//Root
		Node<String> herokuRoot = new Node<String>("Heroku");
		//2
		Node<String> addons = new Node<String>("Addons", herokuRoot);
		//3
		Node<String> categories = new Node<String>("Categories", addons);
		//4
		Node<String> dataStores = new Node<String>("DataStores", categories);
		Node<String> dataStoreUtilities = new Node<String>("DataStoreUtilities", categories);
		Node<String> monitoring = new Node<String>("Monitoring", categories);
		Node<String> logging = new Node<String>("Logging", categories);
		Node<String> emailSMS = new Node<String>("EmailSMS", categories);
		Node<String> caching = new Node<String>("Caching", categories);
		Node<String> errorsAndExceptions = new Node<String>("ErrorsAndExceptions", categories);
		Node<String> contentManagement = new Node<String>("ContentManagement", categories);
		Node<String> search = new Node<String>("Search", categories);
		Node<String> metricsAndAnalytics = new Node<String>("MetricsAndAnalytics", categories);
		Node<String> testing = new Node<String>("Testing", categories);
		Node<String> messagingAndQueueing = new Node<String>("MessagingAndQueueing", categories);
		Node<String> networkServices = new Node<String>("NetworkServices", categories);
		Node<String> alertsAndNotifications = new Node<String>("AlertsAndNotifications", categories);
		Node<String> userManagement = new Node<String>("UserManagement", categories);
		Node<String> developmentTools = new Node<String>("DevelopmentTools", categories);
		Node<String> security = new Node<String>("Security", categories);
		Node<String> dynos = new Node<String>("Dynos", categories);
		Node<String> content = new Node<String>("Content", categories);
		Node<String> documentProcessing = new Node<String>("DocumentProcessing", categories);
		Node<String> imageProcessing = new Node<String>("ImageProcessing", categories);
		Node<String> videoProcessing = new Node<String>("VideoProcessing", categories);
		Node<String> continuousIntegrationAndDelivery = new Node<String>("ContinuousIntegrationAndDelivery", categories);
		Node<String> utilities = new Node<String>("Utilities", categories);
		//5
		void DataStoresInit(){
			dataStores.addChildren(Arrays.asList(
			        new Node<>("Bucketeer"),
			        new Node<>("splicedb"),
			        new Node<>("RedisToGo"),
			        new Node<>("SentinelDB"),
			        new Node<>("RedisCloud"),
			        new Node<>("CloudKarafka"),
			        new Node<>("mLabMongoDB"),
			        new Node<>("ArmTreasureData"),
			        new Node<>("Cloudcube"),
			        new Node<>("JawsDBMaria"),
			        new Node<>("ObjectRocketForMongoDB"),
			        new Node<>("JawsDBMySQL"),
			        new Node<>("CitusData"),
			        new Node<>("HerokuPostgres"),
			        new Node<>("openredis"),
			        new Node<>("ApacheKafkaOnHeroku"),
			        new Node<>("RedisGreen"),
			        new Node<>("InstaclustrApacheCassandra"),
			        new Node<>("GrapheneDB"),
			        new Node<>("SFTPToGo"),
			        new Node<>("InterplanetaryFission"),
			        new Node<>("HDrive"),
			        new Node<>("HerokuRedis"),
			        new Node<>("ClearDBMySQL"),
			        new Node<>("MSSQL")
			));
		}
		
		void MonitoringInit() {
			monitoring.addChildren(Arrays.asList(
					new Node<>("Pingdom"),
					new Node<>("TuneMyGC"),
					new Node<>("DeadMansSnitch"),
					new Node<>("SolarWindsAppOptics"),
					new Node<>("AppDynamics"),
					new Node<>("Librato"),
					new Node<>("HostedGraphite"),
					new Node<>("StillAlive"),
					new Node<>("VigilMonitoringService"),
					new Node<>("NewRelicAPM"),
					new Node<>("ScoutAPM")
			));
		}
		
//		Node<T> getRootNode(){
//			return (Node<T>) heroku;
//		}

		
}
