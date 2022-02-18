package deployer.buildpack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.javabip.annotations.ComponentType;
import org.javabip.annotations.Data;
import org.javabip.annotations.Port;
import org.javabip.annotations.Ports;
import org.javabip.annotations.Transition;
import org.javabip.annotations.Transitions;
import org.javabip.api.PortType;

import deployer.BIPDeployer;

@Ports({
	@Port(name = "setJava", type = PortType.enforceable),
	@Port(name = "setAddonsForJava", type = PortType.enforceable),
	@Port(name = "removeJava", type = PortType.enforceable),
	//
	@Port(name = "setScala", type = PortType.enforceable),
	@Port(name = "setAddonsForScala", type = PortType.enforceable),
	@Port(name = "removeScala", type = PortType.enforceable),
	//
	@Port(name = "setPython", type = PortType.enforceable),
	@Port(name = "setAddonsForPython", type = PortType.enforceable),
	@Port(name = "removePython", type = PortType.enforceable),
	//
	@Port(name = "setRuby", type = PortType.enforceable),
	@Port(name = "setAddonsForRuby", type = PortType.enforceable),
	@Port(name = "removeRuby", type = PortType.enforceable),
	//
	@Port(name = "setNodejs", type = PortType.enforceable),
	@Port(name = "setAddonsForNodejs", type = PortType.enforceable),
	@Port(name = "removeNodejs", type = PortType.enforceable),
	//
	@Port(name = "setClojure", type = PortType.enforceable),
	@Port(name = "setAddonsForClojure", type = PortType.enforceable),
	@Port(name = "removeClojure", type = PortType.enforceable),
	//
	@Port(name = "setGradle", type = PortType.enforceable),
	@Port(name = "setAddonsForGradle", type = PortType.enforceable),
	@Port(name = "removeGradle", type = PortType.enforceable),
	//
	@Port(name = "setJvm", type = PortType.enforceable),
	@Port(name = "setAddonsForJvm", type = PortType.enforceable),
	@Port(name = "removeJvm", type = PortType.enforceable),
	//
	@Port(name = "setPhp", type = PortType.enforceable),
	@Port(name = "setAddonsForPhp", type = PortType.enforceable),
	@Port(name = "removePhp", type = PortType.enforceable),
	//
	@Port(name = "setGo", type = PortType.enforceable),
	@Port(name = "setAddonsForGo", type = PortType.enforceable),
	@Port(name = "removeGo", type = PortType.enforceable),
})

@ComponentType(initial = "Init", name = "deployer.HerokuBuildpack")
public class HerokuBuildpack {
	
	// Java
	@Transitions({
		@Transition(name = "setJava", source = "Init", target = "Java"),
	})
	public void setJava(@Data(name = "AppName") String AppName) throws IOException {	
		System.out.println("[HerokuBuildpack] setJava triggered .... (Init --> Java)");
		String response = callAddAddonAPI("https://github.com/heroku/heroku-buildpack-java", AppName);
		System.out.println("[HerokuBuildpack] setJava respone......." + response);
	}
	//
	@Transitions({
		@Transition(name = "setAddonsForJava", source = "Java", target = "Java"),
	})
	public void setAddonsForJava() {	
		System.out.println("[HerokuBuildpack] setAddonsForJava triggered .... (Java --> Java)");
	}
	//
	
	
	// Scala
	@Transitions({
		@Transition(name = "setScala", source = "Init", target = "Scala"),
	})
	public void setScala(@Data(name = "AppName") String AppName) throws IOException {	
		System.out.println("[HerokuBuildpack] setScala triggered .... (Init --> Scala)");
		String response = callAddAddonAPI("https://github.com/heroku/heroku-buildpack-scala", AppName);
		System.out.println("[HerokuBuildpack] setScala respone......." + response);
	}
	//
	@Transitions({
		@Transition(name = "setAddonsForScala", source = "Scala", target = "Scala"),
	})
	public void setAddonsForScala() {	
		System.out.println("[HerokuBuildpack] setAddonsForScala triggered .... (Scala --> Scala)");
	}
	//
	
	
	// Python
	@Transitions({
		@Transition(name = "setPython", source = "Init", target = "Python"),
	})
	public void setPython(@Data(name = "AppName") String AppName) throws IOException {	
		System.out.println("[HerokuBuildpack] setPython triggered .... (Init --> Python)");
		String response = callAddAddonAPI("https://github.com/heroku/heroku-buildpack-python", AppName);
		System.out.println("[HerokuBuildpack] setPython respone......." + response);
	}
	//
	@Transitions({
		@Transition(name = "setAddonsForPython", source = "Python", target = "Python"),
	})
	public void setAddonsForPython() {	
		System.out.println("[HerokuBuildpack] setAddonsForPython triggered .... (Python --> Python)");
	}
	//
	
	
	// Ruby
	@Transitions({
		@Transition(name = "setRuby", source = "Init", target = "Ruby"),
	})
	public void setRuby(@Data(name = "AppName") String AppName) throws IOException {	
		System.out.println("[HerokuBuildpack] setRuby triggered .... (Init --> Ruby)");
		String response = callAddAddonAPI("https://github.com/heroku/heroku-buildpack-ruby", AppName);
		System.out.println("[HerokuBuildpack] setRuby respone......." + response);
	}
	@Transitions({
		@Transition(name = "setAddonsForRuby", source = "Ruby", target = "Ruby"),
	})
	public void setAddonsForRuby() {	
		System.out.println("[HerokuBuildpack] setAddonsForRuby triggered .... (Ruby --> Ruby)");
	}
	
	
	// Nodejs
	@Transitions({
		@Transition(name = "setNodejs", source = "Init", target = "Nodejs"),
	})
	public void setNodejs(@Data(name = "AppName") String AppName) throws IOException {	
		System.out.println("[HerokuBuildpack] setNodejs triggered .... (Init --> Nodejs)");
		String response = callAddAddonAPI("https://github.com/heroku/heroku-buildpack-nodejs", AppName);
		System.out.println("[HerokuBuildpack] setNodejs respone......." + response);
	}
	@Transitions({
		@Transition(name = "setAddonsForNodejs", source = "Nodejs", target = "Nodejs"),
	})
	public void setAddonsForNodejs() {	
		System.out.println("[HerokuBuildpack] setAddonsForNodejs triggered .... (Nodejs --> Nodejs)");
	}
	//
	
	
	// Clojure
	@Transitions({
		@Transition(name = "setClojure", source = "Init", target = "Clojure"),
	})
	public void setClojure(@Data(name = "AppName") String AppName) throws IOException {	
		System.out.println("[HerokuBuildpack] setClojure triggered .... (Init --> Clojure)");
		String response = callAddAddonAPI("https://github.com/heroku/heroku-buildpack-clojure", AppName);
		System.out.println("[HerokuBuildpack] setClojure respone......." + response);
	}
	@Transitions({
		@Transition(name = "setAddonsForClojure", source = "Clojure", target = "Clojure"),
	})
	public void setAddonsForClojure() {	
		System.out.println("[HerokuBuildpack] setAddonsForClojure triggered .... (Clojure --> Clojure)");
	}
	//
	
	
	// Gradle
	@Transitions({
		@Transition(name = "setGradle", source = "Init", target = "Gradle"),
	})
	public void setGradle(@Data(name = "AppName") String AppName) throws IOException {	
		System.out.println("[HerokuBuildpack] setGradle triggered .... (Init --> Gradle)");
		String response = callAddAddonAPI("https://github.com/heroku/heroku-buildpack-gradle", AppName);
		System.out.println("[HerokuBuildpack] setGradle respone......." + response);
	}
	@Transitions({
		@Transition(name = "setAddonsForGradle", source = "Gradle", target = "Gradle"),
	})
	public void setAddonsForGradle() {	
		System.out.println("[HerokuBuildpack] setAddonsForGradle triggered .... (Gradle --> Gradle)");
	}
	//
	
	
	// Jvm
	@Transitions({
		@Transition(name = "setJvm", source = "Init", target = "Jvm"),
	})
	public void setJvm(@Data(name = "AppName") String AppName) throws IOException {	
		System.out.println("[HerokuBuildpack] setJvm triggered .... (Init --> Jvm)");
		// 
//		String response = callAddAddonAPI("https://github.com/heroku/heroku-buildpack-jvm", AppName);
		String response = callAddAddonAPI("heroku/jvm", AppName);
		System.out.println("[HerokuBuildpack] setJvm respone......." + response);
	}
	@Transitions({
		@Transition(name = "setAddonsForJvm", source = "Jvm", target = "Jvm"),
	})
	public void setAddonsForJvm() {	
		System.out.println("[HerokuBuildpack] setAddonsForJvm triggered .... (Jvm --> Jvm)");
	}
	
	
	// Php
	@Transitions({
		@Transition(name = "setPhp", source = "Init", target = "Php"),
	})
	public void setPhp(@Data(name = "AppName") String AppName) throws IOException {	
		System.out.println("[HerokuBuildpack] setPhp triggered .... (Init --> Php)");
		String response = callAddAddonAPI("https://github.com/heroku/heroku-buildpack-php", AppName);
		System.out.println("[HerokuBuildpack] setPhp respone......." + response);
	}
	@Transitions({
		@Transition(name = "setAddonsForPhp", source = "Php", target = "Php"),
	})
	public void setAddonsForPhp() {	
		System.out.println("[HerokuBuildpack] setAddonsForPhp triggered .... (Php --> Php)");
	}
	//
	
	
	// Go
	@Transitions({
		@Transition(name = "setGo", source = "Init", target = "Go"),
	})
	public void setGo(@Data(name = "AppName") String AppName) throws IOException {	
		System.out.println("[HerokuBuildpack] setGo triggered .... (Init --> Go)");
		String response = callAddAddonAPI("https://github.com/heroku/heroku-buildpack-go", AppName);
		System.out.println("[HerokuBuildpack] setGo respone......." + response);
	}
	@Transitions({
		@Transition(name = "setAddonsForGo", source = "Go", target = "Go"),
	})
	public void setAddonsForGo() {	
		System.out.println("[HerokuBuildpack] setAddonsForGo triggered .... (Go --> Go)");
	}
	
	
	
	//
	@Transitions({
		@Transition(name = "removeJava", source = "Java", target = "Init"),
	})
	public void USreset() {	
		System.out.println("[HerokuBuildpack] removeJava triggered .... (Java --> Init)");
	}

	@Transitions({
		@Transition(name = "removeScala", source = "Scala", target = "Init"),
	})
	public void removeScala() {	
		System.out.println("[HerokuBuildpack] removeScala triggered .... (Scala --> Init)");
	}
	
	@Transitions({
		@Transition(name = "removePython", source = "Python", target = "Init"),
	})
	public void removePython() {	
		System.out.println("[HerokuBuildpack] removePython triggered .... (Python --> Init)");
	}
	
	@Transitions({
		@Transition(name = "removeRuby", source = "Ruby", target = "Init"),
	})
	public void removeRuby() {	
		System.out.println("[HerokuBuildpack] removeRuby triggered .... (Ruby --> Init)");
	}
	
	@Transitions({
		@Transition(name = "removeNodejs", source = "Nodejs", target = "Init"),
	})
	public void removeNodejs() {	
		System.out.println("[HerokuBuildpack] removeNodejs triggered .... (Nodejs --> Init)");
	}
	
	@Transitions({
		@Transition(name = "removeClojure", source = "Clojure", target = "Init"),
	})
	public void removeClojure() {	
		System.out.println("[HerokuBuildpack] removeClojure triggered .... (Clojure --> Init)");
	}
	
	@Transitions({
		@Transition(name = "removeGradle", source = "Gradle", target = "Init"),
	})
	public void removeGradle() {	
		System.out.println("[HerokuBuildpack] removeGradle triggered .... (Gradle --> Init)");
	}
	
	@Transitions({
		@Transition(name = "removeJvm", source = "Jvm", target = "Init"),
	})
	public void removeJvm() {	
		System.out.println("[HerokuBuildpack] removeJvm triggered .... (Jvm --> Init)");
	}
	
	@Transitions({
		@Transition(name = "removePhp", source = "Php", target = "Init"),
	})
	public void removePhp() {	
		System.out.println("[HerokuBuildpack] removePhp triggered .... (Php --> Init)");
	}
	
	@Transitions({
		@Transition(name = "removeGo", source = "Go", target = "Init"),
	})
	public void removeGo() {	
		System.out.println("[HerokuBuildpack] removeGo triggered .... (Go --> Init)");
	}
	
	
	
	// Call Heroku API for adding add-on
	public String callAddAddonAPI(String Addon, String AppName) throws IOException {	
		URL obj = new URL("https://api.heroku.com/apps/" + AppName + "/buildpack-installations");
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("PUT");
		//add request header
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Accept", "application/vnd.heroku+json; version=3");
		con.setRequestProperty("Authorization", "Bearer " + BIPDeployer.authorizationToken);
		con.setDoOutput(true);
		String jsonInputString = "{\r\n" + 
				"  \"updates\": [\r\n" + 
				"    {\r\n" + 
				"      \"buildpack\": \""+ Addon + "\"\r\n" + 
				"    }\r\n" + 
				"  ]\r\n" + 
				"}";
		try(OutputStream os = con.getOutputStream()) {
		    byte[] input = jsonInputString.getBytes("utf-8");
		    os.write(input, 0, input.length);           
		}
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
		//
		return jSONContent.toString();
	}
	
}
