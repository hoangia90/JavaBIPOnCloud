package deployer;

import org.javabip.api.BIPGlue;
import org.javabip.glue.GlueBuilder;

import deployer.buildpack.HerokuBuildpack;
import deployer.herokuAddons.database.HerokuClearDBMySQL;
import deployer.herokuAddons.database.HerokuPostgres;
import deployer.herokuAddons.monitoring.HerokuNewRelicAPM;
import deployer.herokuAddons.monitoring.HerokuScoutAPM;
import deployer.herokuContainer.HerokuDynoType;
import deployer.herokuContainer.HerokuRegion;

public class herokuBIPGlue {
	private BIPGlue bipGlue;
	
	public herokuBIPGlue(BIPGlue bipGlue) {
		this.bipGlue = bipGlue;
	}
	
	public herokuBIPGlue() {
		this.bipGlue = init();
	}
	
	public BIPGlue getBipGlue() {
		return bipGlue;
	}

	public void setBipGlue(BIPGlue bipGlue) {
		this.bipGlue = bipGlue;
	}

	///////////////////////////////////////////////////////////////////////BIP-GLUE END//////////////////////////////////////////////////////////////////
	private BIPGlue init () {
		return bipGlue = new GlueBuilder() {
			@Override
			public void configure() {
				//
				// Need to improve in Data Exchanging: the data will be exchanged before the methods => Should do the way round!. For now the data sent first and then pointing to different data
				//port(HerokuDynoType.class, "chooseConfig").accepts();
				
				////
				port(HerokuDynoType.class, "sub1").requires(Deployer.class, "setFreeDyno");
				port(Deployer.class, "setFreeDyno").requires(HerokuDynoType.class, "sub1");
				port(Deployer.class, "setFreeDyno").accepts(HerokuDynoType.class, "sub1");
				port(HerokuDynoType.class, "sub1").accepts(Deployer.class, "setFreeDyno");
				data(Deployer.class, "DynoOptions").to(HerokuDynoType.class, "DynoOptions");
				////
				port(HerokuDynoType.class, "sendDynoResponse").requires(Deployer.class, "receiveDynoResponse");
				port(Deployer.class, "receiveDynoResponse").requires(HerokuDynoType.class, "sendDynoResponse");
				port(HerokuDynoType.class, "sendDynoResponse").accepts(Deployer.class, "receiveDynoResponse");
				port(Deployer.class, "receiveDynoResponse").accepts(HerokuDynoType.class, "sendDynoResponse");
				data(HerokuDynoType.class, "DynoResponseData").to(Deployer.class, "DynoResponseData");
				//data(Deployer.class, "requestDynoCreatingResponse").to(HerokuDynoType.class, "receiveDynoResponse");
				
				////
				port(HerokuRegion.class, "toUS").requires(Deployer.class, "setUSRegion");
				port(Deployer.class, "setUSRegion").requires(HerokuRegion.class, "toUS");
				port(HerokuRegion.class, "toUS").accepts(Deployer.class, "setUSRegion");
				port(Deployer.class, "setUSRegion").accepts(HerokuRegion.class, "toUS");
				
				port(HerokuRegion.class, "toEU").requires(Deployer.class, "setEURegion");
				port(Deployer.class, "setEURegion").requires(HerokuRegion.class, "toEU");
				port(HerokuRegion.class, "toEU").accepts(Deployer.class, "setEURegion");
				port(Deployer.class, "setEURegion").accepts(HerokuRegion.class, "toEU");
				
				
				//// RDV SYNC -- Might have a problem here -- need to check!!!!
				////Regions
				port(HerokuPostgres.class, "on").requires(HerokuRegion.class, "setAddonsForUS");
				port(HerokuPostgres.class, "on").requires(HerokuRegion.class, "setAddonsForEU");
				port(HerokuClearDBMySQL.class, "on").requires(HerokuRegion.class, "setAddonsForUS");
				port(HerokuClearDBMySQL.class, "on").requires(HerokuRegion.class, "setAddonsForEU");
				port(HerokuScoutAPM.class, "on").requires(HerokuRegion.class, "setAddonsForUS");
				port(HerokuScoutAPM.class, "on").requires(HerokuRegion.class, "setAddonsForEU");
				port(HerokuNewRelicAPM.class, "on").requires(HerokuRegion.class, "setAddonsForUS");
				port(HerokuNewRelicAPM.class, "on").requires(HerokuRegion.class, "setAddonsForEU");
				
				port(HerokuPostgres.class, "on").accepts(
				HerokuRegion.class, "setAddonsForUS",
				HerokuRegion.class, "setAddonsForEU",
				Deployer.class, "setAddonsForUS",
				Deployer.class, "setAddonsForEU",
				//HerokuPostgres.class, "on",
				HerokuClearDBMySQL.class, "on",
				HerokuScoutAPM.class, "on",
				HerokuNewRelicAPM.class, "on"
				);
				port(HerokuClearDBMySQL.class, "on").accepts(
				HerokuRegion.class, "setAddonsForUS",
				HerokuRegion.class, "setAddonsForEU",
				Deployer.class, "setAddonsForUS",
				Deployer.class, "setAddonsForEU",
				HerokuPostgres.class, "on",
				//HerokuClearDBMySQL.class, "on",
				HerokuScoutAPM.class, "on",
				HerokuNewRelicAPM.class, "on"
				);
				port(HerokuScoutAPM.class, "on").accepts(
				HerokuRegion.class, "setAddonsForUS",
				HerokuRegion.class, "setAddonsForEU",
				Deployer.class, "setAddonsForUS",
				Deployer.class, "setAddonsForEU",
				HerokuPostgres.class, "on",
				HerokuClearDBMySQL.class, "on",
				//HerokuScoutAPM.class, "on",
				HerokuNewRelicAPM.class, "on"
				);
				port(HerokuNewRelicAPM.class, "on").accepts(
				HerokuRegion.class, "setAddonsForUS",
				HerokuRegion.class, "setAddonsForEU",
				Deployer.class, "setAddonsForUS",
				Deployer.class, "setAddonsForEU",
				HerokuPostgres.class, "on",
				HerokuClearDBMySQL.class, "on",
				HerokuScoutAPM.class, "on"
				//HerokuNewRelicAPM.class, "on"
				);
				//
				port(HerokuRegion.class, "setAddonsForUS").requires(
				HerokuClearDBMySQL.class, "on", 
				HerokuPostgres.class, "on", 
				HerokuScoutAPM.class, "on", 
				HerokuNewRelicAPM.class, "on", 
				Deployer.class, "setAddonsForUS"
				);
				port(HerokuRegion.class, "setAddonsForEU").requires(
				HerokuClearDBMySQL.class, "on", 
				HerokuPostgres.class, "on", 
				HerokuScoutAPM.class, "on", 
				HerokuNewRelicAPM.class, "on", 
				Deployer.class, "setAddonsForEU"
				);
				port(HerokuRegion.class, "setAddonsForUS").accepts(
				HerokuClearDBMySQL.class, "on", 
				HerokuPostgres.class, "on", 
				HerokuScoutAPM.class, "on", 
				HerokuNewRelicAPM.class, "on", 
				Deployer.class, "setAddonsForUS"
				);
				port(HerokuRegion.class, "setAddonsForEU").accepts(
				HerokuClearDBMySQL.class, "on", 
				HerokuPostgres.class, "on", 
				HerokuScoutAPM.class, "on", 
				HerokuNewRelicAPM.class, "on", 
				Deployer.class, "setAddonsForEU"
				);
				//
				port(Deployer.class, "setAddonsForUS").requires(HerokuRegion.class, "setAddonsForUS");
				port(Deployer.class, "setAddonsForEU").requires(HerokuRegion.class, "setAddonsForEU");
				//
				port(Deployer.class, "setAddonsForUS").accepts(
				HerokuClearDBMySQL.class, "on", 
				HerokuPostgres.class, "on", 
				HerokuScoutAPM.class, "on", 
				HerokuNewRelicAPM.class, "on", 
				HerokuRegion.class, "setAddonsForUS"
				);
				port(Deployer.class, "setAddonsForEU").accepts(
				HerokuClearDBMySQL.class, "on", 
				HerokuPostgres.class, "on", 
				HerokuScoutAPM.class, "on", 
				HerokuNewRelicAPM.class, "on", 
				HerokuRegion.class, "setAddonsForEU"
				);
				
				/////////Buildpacks
				port(Deployer.class, "setJava").requires(HerokuBuildpack.class, "setJava");
				port(Deployer.class, "setScala").requires(HerokuBuildpack.class, "setScala");
				port(Deployer.class, "setPython").requires(HerokuBuildpack.class, "setPython");
				port(Deployer.class, "setRuby").requires(HerokuBuildpack.class, "setRuby");
				port(Deployer.class, "setNodejs").requires(HerokuBuildpack.class, "setNodejs");
				port(Deployer.class, "setClojure").requires(HerokuBuildpack.class, "setClojure");
				port(Deployer.class, "setGradle").requires(HerokuBuildpack.class, "setGradle");
				port(Deployer.class, "setJvm").requires(HerokuBuildpack.class, "setJvm");
				port(Deployer.class, "setPhp").requires(HerokuBuildpack.class, "setPhp");
				port(Deployer.class, "setGo").requires(HerokuBuildpack.class, "setGo");
				port(Deployer.class, "setJava").accepts(HerokuBuildpack.class, "setJava");
				port(Deployer.class, "setScala").accepts(HerokuBuildpack.class, "setScala");
				port(Deployer.class, "setPython").accepts(HerokuBuildpack.class, "setPython");
				port(Deployer.class, "setRuby").accepts(HerokuBuildpack.class, "setRuby");
				port(Deployer.class, "setNodejs").accepts(HerokuBuildpack.class, "setNodejs");
				port(Deployer.class, "setClojure").accepts(HerokuBuildpack.class, "setClojure");
				port(Deployer.class, "setGradle").accepts(HerokuBuildpack.class, "setGradle");
				port(Deployer.class, "setJvm").accepts(HerokuBuildpack.class, "setJvm");
				port(Deployer.class, "setPhp").accepts(HerokuBuildpack.class, "setPhp");
				port(Deployer.class, "setGo").accepts(HerokuBuildpack.class, "setGo");
				
				port(HerokuBuildpack.class, "setJava").requires(Deployer.class, "setJava");
				port(HerokuBuildpack.class, "setScala").requires(Deployer.class, "setScala");
				port(HerokuBuildpack.class, "setPython").requires(Deployer.class, "setPython");
				port(HerokuBuildpack.class, "setRuby").requires(Deployer.class, "setRuby");
				port(HerokuBuildpack.class, "setNodejs").requires(Deployer.class, "setNodejs");
				port(HerokuBuildpack.class, "setClojure").requires(Deployer.class, "setClojure");
				port(HerokuBuildpack.class, "setGradle").requires(Deployer.class, "setGradle");
				port(HerokuBuildpack.class, "setJvm").requires(Deployer.class, "setJvm");
				port(HerokuBuildpack.class, "setPhp").requires(Deployer.class, "setPhp");
				port(HerokuBuildpack.class, "setGo").requires(Deployer.class, "setGo");
				port(HerokuBuildpack.class, "setJava").accepts(Deployer.class, "setJava");
				port(HerokuBuildpack.class, "setScala").accepts(Deployer.class, "setScala");
				port(HerokuBuildpack.class, "setPython").accepts(Deployer.class, "setPython");
				port(HerokuBuildpack.class, "setRuby").accepts(Deployer.class, "setRuby");
				port(HerokuBuildpack.class, "setNodejs").accepts(Deployer.class, "setNodejs");
				port(HerokuBuildpack.class, "setClojure").accepts(Deployer.class, "setClojure");
				port(HerokuBuildpack.class, "setGradle").accepts(Deployer.class, "setGradle");
				port(HerokuBuildpack.class, "setJvm").accepts(Deployer.class, "setJvm");
				port(HerokuBuildpack.class, "setPhp").accepts(Deployer.class, "setPhp");
				port(HerokuBuildpack.class, "setGo").accepts(Deployer.class, "setGo");
				
				
				port(HerokuPostgres.class, "on").requires(HerokuBuildpack.class, "setAddonsForJava");
				port(HerokuPostgres.class, "on").requires(HerokuBuildpack.class, "setAddonsForScala");
				port(HerokuPostgres.class, "on").requires(HerokuBuildpack.class, "setAddonsForPython");
				port(HerokuPostgres.class, "on").requires(HerokuBuildpack.class, "setAddonsForRuby");
				port(HerokuPostgres.class, "on").requires(HerokuBuildpack.class, "setAddonsForNodejs");
				port(HerokuPostgres.class, "on").requires(HerokuBuildpack.class, "setAddonsForClojure");
				port(HerokuPostgres.class, "off").requires(HerokuBuildpack.class, "setAddonsForGradle");
				port(HerokuPostgres.class, "on").requires(HerokuBuildpack.class, "setAddonsForJvm");
				port(HerokuPostgres.class, "on").requires(HerokuBuildpack.class, "setAddonsForPhp");
				port(HerokuPostgres.class, "on").requires(HerokuBuildpack.class, "setAddonsForGo");
				//
				port(HerokuClearDBMySQL.class, "on").requires(HerokuBuildpack.class, "setAddonsForJava");
				port(HerokuClearDBMySQL.class, "on").requires(HerokuBuildpack.class, "setAddonsForScala");
				port(HerokuClearDBMySQL.class, "on").requires(HerokuBuildpack.class, "setAddonsForPython");
				port(HerokuClearDBMySQL.class, "on").requires(HerokuBuildpack.class, "setAddonsForRuby");
				port(HerokuClearDBMySQL.class, "on").requires(HerokuBuildpack.class, "setAddonsForNodejs");
				port(HerokuClearDBMySQL.class, "on").requires(HerokuBuildpack.class, "setAddonsForClojure");
				port(HerokuClearDBMySQL.class, "off").requires(HerokuBuildpack.class, "setAddonsForGradle");
				port(HerokuClearDBMySQL.class, "on").requires(HerokuBuildpack.class, "setAddonsForJvm");
				port(HerokuClearDBMySQL.class, "on").requires(HerokuBuildpack.class, "setAddonsForPhp");
				port(HerokuClearDBMySQL.class, "on").requires(HerokuBuildpack.class, "setAddonsForGo");
				//
				port(HerokuScoutAPM.class, "off").requires(HerokuBuildpack.class, "setAddonsForJava");
				port(HerokuScoutAPM.class, "off").requires(HerokuBuildpack.class, "setAddonsForScala");
				port(HerokuScoutAPM.class, "on").requires(HerokuBuildpack.class, "setAddonsForPython");
				port(HerokuScoutAPM.class, "on").requires(HerokuBuildpack.class, "setAddonsForRuby");
				port(HerokuScoutAPM.class, "off").requires(HerokuBuildpack.class, "setAddonsForNodejs");
				port(HerokuScoutAPM.class, "off").requires(HerokuBuildpack.class, "setAddonsForClojure");
				port(HerokuScoutAPM.class, "off").requires(HerokuBuildpack.class, "setAddonsForGradle");
				port(HerokuScoutAPM.class, "off").requires(HerokuBuildpack.class, "setAddonsForJvm");
				port(HerokuScoutAPM.class, "on").requires(HerokuBuildpack.class, "setAddonsForPhp");
				port(HerokuScoutAPM.class, "off").requires(HerokuBuildpack.class, "setAddonsForGo");
				//
				port(HerokuNewRelicAPM.class, "on").requires(HerokuBuildpack.class, "setAddonsForJava");
				port(HerokuNewRelicAPM.class, "off").requires(HerokuBuildpack.class, "setAddonsForScala");
				port(HerokuNewRelicAPM.class, "on").requires(HerokuBuildpack.class, "setAddonsForPython");
				port(HerokuNewRelicAPM.class, "on").requires(HerokuBuildpack.class, "setAddonsForRuby");
				port(HerokuNewRelicAPM.class, "on").requires(HerokuBuildpack.class, "setAddonsForNodejs");
				port(HerokuNewRelicAPM.class, "off").requires(HerokuBuildpack.class, "setAddonsForClojure");
				port(HerokuNewRelicAPM.class, "off").requires(HerokuBuildpack.class, "setAddonsForGradle");
				port(HerokuNewRelicAPM.class, "on").requires(HerokuBuildpack.class, "setAddonsForJvm");
				port(HerokuNewRelicAPM.class, "on").requires(HerokuBuildpack.class, "setAddonsForPhp");
				port(HerokuNewRelicAPM.class, "off").requires(HerokuBuildpack.class, "setAddonsForGo");
				//
				port(HerokuPostgres.class, "on").accepts(
				HerokuBuildpack.class, "setAddonsForJava",
				HerokuBuildpack.class, "setAddonsForScala",
				HerokuBuildpack.class, "setAddonsForPython",
				HerokuBuildpack.class, "setAddonsForRuby",
				HerokuBuildpack.class, "setAddonsForNodejs",
				HerokuBuildpack.class, "setAddonsForClojure",
				HerokuBuildpack.class, "setAddonsForGradle",
				HerokuBuildpack.class, "setAddonsForJvm",
				HerokuBuildpack.class, "setAddonsForPhp",
				HerokuBuildpack.class, "setAddonsForGo",
				//
				Deployer.class, "setAddonsForJava",
				Deployer.class, "setAddonsForScala",
				Deployer.class, "setAddonsForPython",
				Deployer.class, "setAddonsForRuby",
				Deployer.class, "setAddonsForNodejs",
				Deployer.class, "setAddonsForClojure",
				Deployer.class, "setAddonsForGradle",
				Deployer.class, "setAddonsForJvm",
				Deployer.class, "setAddonsForPhp",
				Deployer.class, "setAddonsForGo",
				////
				HerokuPostgres.class, "on",
				HerokuClearDBMySQL.class, "on",
				HerokuScoutAPM.class, "off",
				HerokuNewRelicAPM.class, "on",
				HerokuNewRelicAPM.class, "off"
				
				);
				port(HerokuClearDBMySQL.class, "on").accepts(
				HerokuBuildpack.class, "setAddonsForJava",
				HerokuBuildpack.class, "setAddonsForScala",
				HerokuBuildpack.class, "setAddonsForPython",
				HerokuBuildpack.class, "setAddonsForRuby",
				HerokuBuildpack.class, "setAddonsForNodejs",
				HerokuBuildpack.class, "setAddonsForClojure",
				HerokuBuildpack.class, "setAddonsForGradle",
				HerokuBuildpack.class, "setAddonsForJvm",
				HerokuBuildpack.class, "setAddonsForPhp",
				HerokuBuildpack.class, "setAddonsForGo",
				//
				Deployer.class, "setAddonsForJava",
				Deployer.class, "setAddonsForScala",
				Deployer.class, "setAddonsForPython",
				Deployer.class, "setAddonsForRuby",
				Deployer.class, "setAddonsForNodejs",
				Deployer.class, "setAddonsForClojure",
				Deployer.class, "setAddonsForGradle",
				Deployer.class, "setAddonsForJvm",
				Deployer.class, "setAddonsForPhp",
				Deployer.class, "setAddonsForGo",
				//
				HerokuPostgres.class, "on",
				//HerokuClearDBMySQL.class, "on",
				HerokuScoutAPM.class, "off",
				HerokuNewRelicAPM.class, "on",
				HerokuNewRelicAPM.class, "off"
				);
				port(HerokuScoutAPM.class, "off").accepts(
				HerokuBuildpack.class, "setAddonsForJava",
				HerokuBuildpack.class, "setAddonsForScala",
				HerokuBuildpack.class, "setAddonsForPython",
				HerokuBuildpack.class, "setAddonsForRuby",
				HerokuBuildpack.class, "setAddonsForNodejs",
				HerokuBuildpack.class, "setAddonsForClojure",
				HerokuBuildpack.class, "setAddonsForGradle",
				HerokuBuildpack.class, "setAddonsForJvm",
				HerokuBuildpack.class, "setAddonsForPhp",
				HerokuBuildpack.class, "setAddonsForGo",
				//
				Deployer.class, "setAddonsForJava",
				Deployer.class, "setAddonsForScala",
				Deployer.class, "setAddonsForPython",
				Deployer.class, "setAddonsForRuby",
				Deployer.class, "setAddonsForNodejs",
				Deployer.class, "setAddonsForClojure",
				Deployer.class, "setAddonsForGradle",
				Deployer.class, "setAddonsForJvm",
				Deployer.class, "setAddonsForPhp",
				Deployer.class, "setAddonsForGo",
				//
				HerokuPostgres.class, "on",
				HerokuClearDBMySQL.class, "on",
				//HerokuScoutAPM.class, "off",
				HerokuNewRelicAPM.class, "on",
				HerokuNewRelicAPM.class, "off"
				);
				port(HerokuNewRelicAPM.class, "on").accepts(
				HerokuBuildpack.class, "setAddonsForJava",
				HerokuBuildpack.class, "setAddonsForScala",
				HerokuBuildpack.class, "setAddonsForPython",
				HerokuBuildpack.class, "setAddonsForRuby",
				HerokuBuildpack.class, "setAddonsForNodejs",
				HerokuBuildpack.class, "setAddonsForClojure",
				HerokuBuildpack.class, "setAddonsForGradle",
				HerokuBuildpack.class, "setAddonsForJvm",
				HerokuBuildpack.class, "setAddonsForPhp",
				HerokuBuildpack.class, "setAddonsForGo",
				//
				Deployer.class, "setAddonsForJava",
				Deployer.class, "setAddonsForScala",
				Deployer.class, "setAddonsForPython",
				Deployer.class, "setAddonsForRuby",
				Deployer.class, "setAddonsForNodejs",
				Deployer.class, "setAddonsForClojure",
				Deployer.class, "setAddonsForGradle",
				Deployer.class, "setAddonsForJvm",
				Deployer.class, "setAddonsForPhp",
				Deployer.class, "setAddonsForGo",
				//
				HerokuPostgres.class, "on",
				HerokuClearDBMySQL.class, "on",
				HerokuScoutAPM.class, "off"
				//HerokuNewRelicAPM.class, "on",
				//HerokuNewRelicAPM.class, "off"
				);
				port(HerokuNewRelicAPM.class, "off").accepts(
				HerokuBuildpack.class, "setAddonsForJava",
				HerokuBuildpack.class, "setAddonsForScala",
				HerokuBuildpack.class, "setAddonsForPython",
				HerokuBuildpack.class, "setAddonsForRuby",
				HerokuBuildpack.class, "setAddonsForNodejs",
				HerokuBuildpack.class, "setAddonsForClojure",
				HerokuBuildpack.class, "setAddonsForGradle",
				HerokuBuildpack.class, "setAddonsForJvm",
				HerokuBuildpack.class, "setAddonsForPhp",
				HerokuBuildpack.class, "setAddonsForGo",
				//
				Deployer.class, "setAddonsForJava",
				Deployer.class, "setAddonsForScala",
				Deployer.class, "setAddonsForPython",
				Deployer.class, "setAddonsForRuby",
				Deployer.class, "setAddonsForNodejs",
				Deployer.class, "setAddonsForClojure",
				Deployer.class, "setAddonsForGradle",
				Deployer.class, "setAddonsForJvm",
				Deployer.class, "setAddonsForPhp",
				Deployer.class, "setAddonsForGo",
				//
				HerokuPostgres.class, "on",
				HerokuClearDBMySQL.class, "on",
				HerokuScoutAPM.class, "off"
				//HerokuNewRelicAPM.class, "on"
				//HerokuNewRelicAPM.class, "off"
				);
				//
				port(HerokuBuildpack.class, "setAddonsForJava").requires(
				Deployer.class, "setAddonsForJava",
				HerokuClearDBMySQL.class, "on", 
				HerokuPostgres.class, "on", 
				HerokuScoutAPM.class, "off", 
				HerokuNewRelicAPM.class, "on"
				);
				port(HerokuBuildpack.class, "setAddonsForScala").requires(
				Deployer.class, "setAddonsForScala",
				HerokuClearDBMySQL.class, "on", 
				HerokuPostgres.class, "on",
				HerokuScoutAPM.class, "off", 
				HerokuNewRelicAPM.class, "off"
				);
				port(HerokuBuildpack.class, "setAddonsForPython").requires(
				Deployer.class, "setAddonsForPython",
				HerokuClearDBMySQL.class, "on", 
				HerokuPostgres.class, "on",
				HerokuScoutAPM.class, "on", 
				HerokuNewRelicAPM.class, "on"
				);
				port(HerokuBuildpack.class, "setAddonsForRuby").requires(
				Deployer.class, "setAddonsForRuby",
				HerokuClearDBMySQL.class, "on", 
				HerokuPostgres.class, "on",
				HerokuScoutAPM.class, "on", 
				HerokuNewRelicAPM.class, "on"
				);
				port(HerokuBuildpack.class, "setAddonsForNodejs").requires(
				Deployer.class, "setAddonsForNodejs",
				HerokuClearDBMySQL.class, "on", 
				HerokuPostgres.class, "on",
				HerokuScoutAPM.class, "off", 
				HerokuNewRelicAPM.class, "on"
				);
				port(HerokuBuildpack.class, "setAddonsForClojure").requires(
				Deployer.class, "setAddonsForClojure",
				HerokuClearDBMySQL.class, "on", 
				HerokuPostgres.class, "on",
				HerokuScoutAPM.class, "off", 
				HerokuNewRelicAPM.class, "off"
				);
				port(HerokuBuildpack.class, "setAddonsForGradle").requires(
				Deployer.class, "setAddonsForGradle",
				HerokuClearDBMySQL.class, "off", 
				HerokuPostgres.class, "off",
				HerokuScoutAPM.class, "off", 
				HerokuNewRelicAPM.class, "off"
				);
				port(HerokuBuildpack.class, "setAddonsForJvm").requires(
				Deployer.class, "setAddonsForJvm",
				HerokuClearDBMySQL.class, "on", 
				HerokuPostgres.class, "on", 
				HerokuScoutAPM.class, "off", 
				HerokuNewRelicAPM.class, "on"
				);
				port(HerokuBuildpack.class, "setAddonsForPhp").requires(
				Deployer.class, "setAddonsForPhp",
				HerokuClearDBMySQL.class, "on", 
				HerokuPostgres.class, "on",
				HerokuScoutAPM.class, "on", 
				HerokuNewRelicAPM.class, "on"
				);
				port(HerokuBuildpack.class, "setAddonsForGo").requires(
				Deployer.class, "setAddonsForGo",
				HerokuClearDBMySQL.class, "on", 
				HerokuPostgres.class, "on",
				HerokuScoutAPM.class, "off", 
				HerokuNewRelicAPM.class, "on"
				);
				//
				port(HerokuBuildpack.class, "setAddonsForJava").accepts(
				Deployer.class, "setAddonsForJava",
				HerokuClearDBMySQL.class, "on", 
				HerokuPostgres.class, "on", 
				HerokuScoutAPM.class, "off", 
				HerokuNewRelicAPM.class, "on"
				);
				port(HerokuBuildpack.class, "setAddonsForScala").accepts(
				Deployer.class, "setAddonsForScala",
				HerokuClearDBMySQL.class, "on", 
				HerokuPostgres.class, "on",
				HerokuScoutAPM.class, "off", 
				HerokuNewRelicAPM.class, "off"
				);
				port(HerokuBuildpack.class, "setAddonsForPython").accepts(
				Deployer.class, "setAddonsForPython",
				HerokuClearDBMySQL.class, "on", 
				HerokuPostgres.class, "on",
				HerokuScoutAPM.class, "on", 
				HerokuNewRelicAPM.class, "on"
				);
				port(HerokuBuildpack.class, "setAddonsForRuby").accepts(
				Deployer.class, "setAddonsForRuby",
				HerokuClearDBMySQL.class, "on", 
				HerokuPostgres.class, "on",
				HerokuScoutAPM.class, "on", 
				HerokuNewRelicAPM.class, "on"
				);
				port(HerokuBuildpack.class, "setAddonsForNodejs").accepts(
				Deployer.class, "setAddonsForNodejs",
				HerokuClearDBMySQL.class, "on", 
				HerokuPostgres.class, "on",
				HerokuScoutAPM.class, "off", 
				HerokuNewRelicAPM.class, "on"
				);
				port(HerokuBuildpack.class, "setAddonsForClojure").accepts(
				Deployer.class, "setAddonsForClojure",
				HerokuClearDBMySQL.class, "on", 
				HerokuPostgres.class, "on",
				HerokuScoutAPM.class, "off", 
				HerokuNewRelicAPM.class, "off"
				);
				port(HerokuBuildpack.class, "setAddonsForGradle").accepts(
				Deployer.class, "setAddonsForGradle",
				HerokuClearDBMySQL.class, "off", 
				HerokuPostgres.class, "off",
				HerokuScoutAPM.class, "off", 
				HerokuNewRelicAPM.class, "off"
				);
				port(HerokuBuildpack.class, "setAddonsForJvm").accepts(
				Deployer.class, "setAddonsForJvm",
				HerokuClearDBMySQL.class, "on", 
				HerokuPostgres.class, "on", 
				HerokuScoutAPM.class, "off", 
				HerokuNewRelicAPM.class, "on"
				);
				port(HerokuBuildpack.class, "setAddonsForPhp").accepts(
				Deployer.class, "setAddonsForPhp",
				HerokuClearDBMySQL.class, "on", 
				HerokuPostgres.class, "on",
				HerokuScoutAPM.class, "on", 
				HerokuNewRelicAPM.class, "on"
				);
				port(HerokuBuildpack.class, "setAddonsForGo").accepts(
				Deployer.class, "setAddonsForGo",
				HerokuClearDBMySQL.class, "on", 
				HerokuPostgres.class, "on",
				HerokuScoutAPM.class, "off", 
				HerokuNewRelicAPM.class, "on"
				);
				//
				port(Deployer.class, "setAddonsForJava").requires(HerokuBuildpack.class, "setAddonsForJava");
				port(Deployer.class, "setAddonsForScala").requires(HerokuBuildpack.class, "setAddonsForScala");
				port(Deployer.class, "setAddonsForPython").requires(HerokuBuildpack.class, "setAddonsForPython");
				port(Deployer.class, "setAddonsForRuby").requires(HerokuBuildpack.class, "setAddonsForRuby");
				port(Deployer.class, "setAddonsForNodejs").requires(HerokuBuildpack.class, "setAddonsForNodejs");
				port(Deployer.class, "setAddonsForClojure").requires(HerokuBuildpack.class, "setAddonsForClojure");
				port(Deployer.class, "setAddonsForGradle").requires(HerokuBuildpack.class, "setAddonsForGradle");
				port(Deployer.class, "setAddonsForJvm").requires(HerokuBuildpack.class, "setAddonsForJvm");
				port(Deployer.class, "setAddonsForPhp").requires(HerokuBuildpack.class, "setAddonsForPhp");
				port(Deployer.class, "setAddonsForGo").requires(HerokuBuildpack.class, "setAddonsForGo");
				//
				port(Deployer.class, "setAddonsForJava").accepts(
				HerokuBuildpack.class, "setAddonsForJava",
				HerokuClearDBMySQL.class, "on", 
				HerokuPostgres.class, "on", 
				HerokuScoutAPM.class, "off", 
				HerokuNewRelicAPM.class, "on"
				);
				port(Deployer.class, "setAddonsForScala").accepts(
				HerokuBuildpack.class, "setAddonsForScala",
				HerokuClearDBMySQL.class, "on", 
				HerokuPostgres.class, "on",
				HerokuScoutAPM.class, "off", 
				HerokuNewRelicAPM.class, "off"
				);
				port(Deployer.class, "setAddonsForPython").accepts(
				HerokuBuildpack.class, "setAddonsForPython",
				HerokuClearDBMySQL.class, "on", 
				HerokuPostgres.class, "on",
				HerokuScoutAPM.class, "on", 
				HerokuNewRelicAPM.class, "on"
				);
				port(Deployer.class, "setAddonsForRuby").accepts(
				HerokuBuildpack.class, "setAddonsForRuby",
				HerokuClearDBMySQL.class, "on", 
				HerokuPostgres.class, "on",
				HerokuScoutAPM.class, "on", 
				HerokuNewRelicAPM.class, "on"
				);
				port(Deployer.class, "setAddonsForNodejs").accepts(
				HerokuBuildpack.class, "setAddonsForNodejs",
				HerokuClearDBMySQL.class, "on", 
				HerokuPostgres.class, "on",
				HerokuScoutAPM.class, "off", 
				HerokuNewRelicAPM.class, "on"
				);
				port(Deployer.class, "setAddonsForClojure").accepts(
				HerokuBuildpack.class, "setAddonsForClojure",
				HerokuClearDBMySQL.class, "on", 
				HerokuPostgres.class, "on",
				HerokuScoutAPM.class, "off", 
				HerokuNewRelicAPM.class, "off"
				);
				port(Deployer.class, "setAddonsForGradle").accepts(
				HerokuBuildpack.class, "setAddonsForGradle",
				HerokuClearDBMySQL.class, "off", 
				HerokuPostgres.class, "off",
				HerokuScoutAPM.class, "off", 
				HerokuNewRelicAPM.class, "off"
				);
				port(Deployer.class, "setAddonsForJvm").accepts(
				HerokuBuildpack.class, "setAddonsForJvm",
				HerokuClearDBMySQL.class, "on", 
				HerokuPostgres.class, "on", 
				HerokuScoutAPM.class, "off", 
				HerokuNewRelicAPM.class, "on"
				);
				port(Deployer.class, "setAddonsForPhp").accepts(
				HerokuBuildpack.class, "setAddonsForPhp",
				HerokuClearDBMySQL.class, "on", 
				HerokuPostgres.class, "on",
				HerokuScoutAPM.class, "on", 
				HerokuNewRelicAPM.class, "on"
				);
				port(Deployer.class, "setAddonsForGo").accepts(
				HerokuBuildpack.class, "setAddonsForGo",
				HerokuClearDBMySQL.class, "on", 
				HerokuPostgres.class, "on",
				HerokuScoutAPM.class, "off", 
				HerokuNewRelicAPM.class, "on"
				);
				//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				
				////
				data(Deployer.class, "AppName").to(HerokuBuildpack.class, "AppName");
				
				port(HerokuBuildpack.class, "setJava").requires(Deployer.class, "setJava");
				port(HerokuBuildpack.class, "setJava").accepts(Deployer.class, "setJava");
				port(Deployer.class, "setJava").requires(HerokuBuildpack.class, "setJava");
				port(Deployer.class, "setJava").accepts(HerokuBuildpack.class, "setJava");
				
				port(HerokuBuildpack.class, "setScala").requires(Deployer.class, "setScala");
				port(HerokuBuildpack.class, "setScala").accepts(Deployer.class, "setScala");
				port(Deployer.class, "setScala").requires(HerokuBuildpack.class, "setScala");
				port(Deployer.class, "setScala").accepts(HerokuBuildpack.class, "setScala");
				
				port(HerokuBuildpack.class, "setPython").requires(Deployer.class, "setPython");
				port(HerokuBuildpack.class, "setPython").accepts(Deployer.class, "setPython");
				port(Deployer.class, "setPython").requires(HerokuBuildpack.class, "setPython");
				port(Deployer.class, "setPython").accepts(HerokuBuildpack.class, "setPython");
				
				port(HerokuBuildpack.class, "setGradle").requires(Deployer.class, "setGradle");
				port(HerokuBuildpack.class, "setGradle").accepts(Deployer.class, "setGradle");
				port(Deployer.class, "setGradle").requires(HerokuBuildpack.class, "setGradle");
				port(Deployer.class, "setGradle").accepts(HerokuBuildpack.class, "setGradle");
				
				port(HerokuBuildpack.class, "setJvm").requires(Deployer.class, "setJvm");
				port(HerokuBuildpack.class, "setJvm").accepts(Deployer.class, "setJvm");
				port(Deployer.class, "setJvm").requires(HerokuBuildpack.class, "setJvm");
				port(Deployer.class, "setJvm").accepts(HerokuBuildpack.class, "setJvm");
				
				port(HerokuBuildpack.class, "setPhp").requires(Deployer.class, "setPhp");
				port(HerokuBuildpack.class, "setPhp").accepts(Deployer.class, "setPhp");
				port(Deployer.class, "setPhp").requires(HerokuBuildpack.class, "setPhp");
				port(Deployer.class, "setPhp").accepts(HerokuBuildpack.class, "setPhp");
				
				port(HerokuBuildpack.class, "setGo").requires(Deployer.class, "setGo");
				port(HerokuBuildpack.class, "setGo").accepts(Deployer.class, "setGo");
				port(Deployer.class, "setGo").requires(HerokuBuildpack.class, "setGo");
				port(Deployer.class, "setGo").accepts(HerokuBuildpack.class, "setGo");
				
				////
				data(Deployer.class, "AppName").to(HerokuPostgres.class, "AppName");
				data(HerokuPostgres.class, "AddonResponse").to(Deployer.class, "AddonResponse");
				
				port(Deployer.class, "receiveAddonResponse").requires(HerokuPostgres.class, "sendAddonResponse");
				port(Deployer.class, "receiveAddonResponse").accepts(HerokuPostgres.class, "sendAddonResponse");			
				//port(HerokuPostgres.class, "sendAddonResponse").requires(Deployer.class, "receiveAddonResponse");
				//port(HerokuPostgres.class, "sendAddonResponse").accepts(Deployer.class, "receiveAddonResponse");
				
				//port(Deployer.class, "addHerokuPostgres1").requires(HerokuPostgres.class, "sub1");
				port(Deployer.class, "addHerokuPostgres1").accepts(HerokuPostgres.class, "sub1");
				port(HerokuPostgres.class, "sub1").requires(Deployer.class, "addHerokuPostgres1");
				port(HerokuPostgres.class, "sub1").accepts(Deployer.class, "addHerokuPostgres1");
				
				//port(Deployer.class, "addHerokuPostgres2").requires(HerokuPostgres.class, "sub2");
				port(Deployer.class, "addHerokuPostgres2").accepts(HerokuPostgres.class, "sub2");
				port(HerokuPostgres.class, "sub2").requires(Deployer.class, "addHerokuPostgres2");
				port(HerokuPostgres.class, "sub2").accepts(Deployer.class, "addHerokuPostgres2");
				
				//port(Deployer.class, "addClearDBMySQL1").requires(HerokuClearDBMySQL.class, "sub1");
				port(Deployer.class, "addClearDBMySQL1").accepts(HerokuClearDBMySQL.class, "sub1");
				port(HerokuClearDBMySQL.class, "sub1").requires(Deployer.class, "addClearDBMySQL1");
				port(HerokuClearDBMySQL.class, "sub1").accepts(Deployer.class, "addClearDBMySQL1");
				
				
				
				//port(Deployer.class, "addScoutAPM1").requires(HerokuScoutAPM.class, "sub1");
				port(Deployer.class, "addScoutAPM1").accepts(HerokuScoutAPM.class, "sub1");
				port(HerokuScoutAPM.class, "sub1").requires(Deployer.class, "addScoutAPM1");
				port(HerokuScoutAPM.class, "sub1").accepts(Deployer.class, "addScoutAPM1");
				
				//port(Deployer.class, "addNewRelicAPM1").requires(HerokuNewRelicAPM.class, "sub1");
				port(Deployer.class, "addNewRelicAPM1").accepts(HerokuNewRelicAPM.class, "sub1");
				port(HerokuNewRelicAPM.class, "sub1").requires(Deployer.class, "addNewRelicAPM1");
				port(HerokuNewRelicAPM.class, "sub1").accepts(Deployer.class, "addNewRelicAPM1");
				////
				port(Deployer.class, "pushAppToContainer").requiresNothing();
				port(Deployer.class, "pushAppToContainer").accepts();
				////
				
				port(HerokuDynoType.class, "reset1").requires(Deployer.class, "resetAll");
				//
				port(HerokuRegion.class, "USreset").requires(Deployer.class, "resetAll");
				port(HerokuRegion.class, "EUreset").requires(Deployer.class, "resetAll");
				//
				port(HerokuBuildpack.class, "removeJava").requires(Deployer.class, "resetAll");
				port(HerokuBuildpack.class, "removeScala").requires(Deployer.class, "resetAll");
				port(HerokuBuildpack.class, "removeJvm").requires(Deployer.class, "resetAll");
				port(HerokuBuildpack.class, "removePython").requires(Deployer.class, "resetAll");
				port(HerokuBuildpack.class, "removeRuby").requires(Deployer.class, "resetAll");
				port(HerokuBuildpack.class, "removeNodejs").requires(Deployer.class, "resetAll");
				port(HerokuBuildpack.class, "removeClojure").requires(Deployer.class, "resetAll");
				port(HerokuBuildpack.class, "removeGradle").requires(Deployer.class, "resetAll");
				port(HerokuBuildpack.class, "removePhp").requires(Deployer.class, "resetAll");
				port(HerokuBuildpack.class, "removeGo").requires(Deployer.class, "resetAll");
				
				//
				port(HerokuPostgres.class, "off").requires(Deployer.class, "resetAll");
				port(HerokuPostgres.class, "reset1").requires(Deployer.class, "resetAll");
				port(HerokuPostgres.class, "reset2").requires(Deployer.class, "resetAll");
				port(HerokuClearDBMySQL.class, "off").requires(Deployer.class, "resetAll");
				port(HerokuClearDBMySQL.class, "reset1").requires(Deployer.class, "resetAll");
				port(HerokuScoutAPM.class, "off").requires(Deployer.class, "resetAll");
				port(HerokuScoutAPM.class, "reset1").requires(Deployer.class, "resetAll");
				port(HerokuNewRelicAPM.class, "off").requires(Deployer.class, "resetAll");
				port(HerokuNewRelicAPM.class, "reset1").requires(Deployer.class, "resetAll");
				//
				port(Deployer.class, "resetAll").accepts(
				HerokuDynoType.class, "reset1", 
				HerokuRegion.class, "USreset", 
				HerokuRegion.class, "EUreset", 
				HerokuBuildpack.class, "removeJava",
				HerokuBuildpack.class, "removeScala",
				HerokuBuildpack.class, "removeJvm",
				HerokuBuildpack.class, "removePython",
				HerokuBuildpack.class, "removeRuby",
				HerokuBuildpack.class, "removeNodejs",
				HerokuBuildpack.class, "removeClojure",
				HerokuBuildpack.class, "removeGradle",
				HerokuBuildpack.class, "removePhp",
				HerokuBuildpack.class, "removeGo",
				HerokuPostgres.class, "off", HerokuPostgres.class, "reset1",
				HerokuPostgres.class, "off", HerokuPostgres.class, "reset2",
				HerokuClearDBMySQL.class, "off", HerokuClearDBMySQL.class, "reset1",
				HerokuScoutAPM.class, "off", HerokuScoutAPM.class, "reset1",
				HerokuNewRelicAPM.class, "off", HerokuNewRelicAPM.class, "reset1"
				);
				//////
				
				
				port(Deployer.class, "deleteContainer").acceptsNothing();
				port(Deployer.class, "deleteContainer").requiresNothing();
				
				port(Deployer.class, "dynoCreatingError").requiresNothing();
				port(Deployer.class, "dynoCreatingError").acceptsNothing();
			}
		}.build();
	}
	///////////////////////////////////////////////////////////////////////BIP-GLUE END//////////////////////////////////////////////////////////////////
}
