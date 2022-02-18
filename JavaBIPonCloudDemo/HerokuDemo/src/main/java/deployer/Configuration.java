package deployer;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class Configuration {
	private boolean acm = false;
	private String Buildpack = "";
	private String StackName = "";
	private String StackId = "";
	private String GitUrl = "";
	private String AppId = "";
	private String AppName = "";
	private boolean InternalRouting = false;
	private boolean Maintenance = false;
	private String OwnerEmail = "";
	private String OwnerID = "";
	private String OrganizationID = "";
	private String OrganizationName = "";
	private String TeamID = "";
	private String TeamName = "";
	private String RegionID = "";
	private String RegionName = "";
	private String SpaceID = ""; 
	private String SpaceName = "";
	private boolean SpaceShield = false;
	private String WebUrl = "";
	private ArrayList<String[]> Addons = new ArrayList<String[]>();
//	String database_url = "";
	
	//
	boolean pushAppSkip = false;
	private ArrayList<String> deployStatus = new ArrayList<String>();
	
	
	public boolean isAcm() {
		return acm;
	}

	public void setAcm(boolean acm) {
		this.acm = acm;
	}

//	public String getDatabase_url() {
//		return database_url;
//	}
//
//	public void setDatabase_url(String database_url) {
//		this.database_url = database_url;
//	}

	public Configuration(String Buildpack, ArrayList<String[]> Addons) {
		this.Buildpack = Buildpack;
		this.Addons = (ArrayList<String[]>) Addons;
	}
	
	public Configuration(String RegionName, String buildpack, ArrayList<String[]> addons) {
		this.RegionName = RegionName;
		this.Buildpack = buildpack;
		this.Addons = (ArrayList<String[]>) addons;
	}
	
	public void setFreeDynoInfo(boolean acm, String Buildpack, String StackId, String StackName, String GitUrl, String AppId, String AppName, 
			boolean InternalRouting, boolean Maintenance, String OwnerEmail, String OwnerID, String OrganizationID, String OrganizationName,
			String TeamID, String TeamName, String RegionID, String RegionName, String SpaceID, String SpaceName, boolean SpaceShield, String WebUrl) 
	{
		this.acm = acm;
		this.Buildpack = Buildpack;
		this.StackId = StackId;
		this.StackName = StackName;
		this.GitUrl = GitUrl;
		this.AppId = AppId;
		this.AppName = AppName;
		this.InternalRouting = InternalRouting;
		this.Maintenance = Maintenance;
		this.OwnerEmail = OwnerEmail;
		this.OwnerID = OwnerID;
		this.OrganizationID = OrganizationID;
		this.OrganizationName = OrganizationName;
		this.TeamID = TeamID;
		this.TeamName = TeamName;
		this.RegionID = RegionID;
		this.RegionName = RegionName;
		this.SpaceID = SpaceID;
		this.SpaceName = SpaceName;
		this.SpaceShield = SpaceShield;
		this.WebUrl = WebUrl;
	}
	
	
	public void setNull() 
	{
		this.acm = false;
		this.Buildpack = null;
		this.StackId = null;
		this.StackName = null;
		this.GitUrl = null;
		this.AppId = null;
		this.AppName = null;
		this.InternalRouting = false;
		this.Maintenance = false;
		this.OwnerEmail = null;
		this.OwnerID = null;
		this.OrganizationID = null;
		this.OrganizationName = null;
		this.TeamID = null;
		this.TeamName = null;
		this.RegionID = null;
		this.RegionName = null;
		this.SpaceID = null;
		this.SpaceName = null;
		this.SpaceShield = false;
		this.WebUrl = null;
		this.Addons = null;
	}
	
	public void setFreeDynoInfoJson(String jsonString) {
		JSONObject obj = new JSONObject(jsonString);
		
		this.acm = obj.getBoolean("acm");
		
//		if (!this.Buildpack.isEmpty()) {
//			this.Buildpack = obj.get("buildpack_provided_description").toString();
//		}
		
		this.StackId = obj.getJSONObject("stack").getString("id");
		this.StackName = obj.getJSONObject("stack").getString("name");
		this.GitUrl = obj.getString("git_url");
		this.AppId = obj.getString("id");
		this.AppName = obj.getString("name");
		
		if (obj.has("internal_routing") && !obj.isNull("internal_routing")) {
			this.InternalRouting = obj.getBoolean("internal_routing");
		}
		
		this.Maintenance = obj.getBoolean("maintenance");
		this.OwnerEmail = obj.getJSONObject("owner").getString("email");
		this.OwnerID = obj.getJSONObject("owner").getString("id");
		
		if (obj.has("organization") && !obj.isNull("organization")) {
			this.OrganizationID = obj.getJSONObject("organization").getString("id");
			this.OrganizationName = obj.getJSONObject("organization").getString("name");
		}
		
		if (obj.has("team") && !obj.isNull("team")) {
			this.TeamID = obj.getJSONObject("team").getString("id");
			this.TeamName = obj.getJSONObject("team").getString("name");
		}
		
		this.RegionID = obj.getJSONObject("region").getString("id");
		this.RegionName = obj.getJSONObject("region").getString("name");
		
		if (obj.has("space") && !obj.isNull("space")) {
			this.SpaceID = obj.getJSONObject("space").getString("id");
			this.SpaceName = obj.getJSONObject("space").getString("name");
			this.SpaceShield = obj.getJSONObject("space").getBoolean("shield");
		}
		
		this.WebUrl = obj.getString("web_url");
		
		System.out.println(acm + "\n" + Buildpack + "\n" + StackId + "\n" + StackName + "\n" +
				GitUrl + "\n" + AppId + "\n" + AppName + "\n" + "InternalRouting" + "\n" + Maintenance + "\n" +
				OwnerEmail + "\n" + OwnerID + "\n" + OrganizationID + "\n" + OrganizationName + "\n" + TeamID + "\n" + TeamName + "\n" + 
				RegionID + "\n" + RegionName + "\n" + SpaceID + "\n" + SpaceName + "\n" + SpaceShield + "\n" + WebUrl);
	}
	
	
	public boolean isacm() {
		return acm;
	}

	public void setacm(boolean acm) {
		this.acm = acm;
	}

	public String getStackName() {
		return StackName;
	}

	public void setStackName(String stackName) {
		StackName = stackName;
	}

	public String getStackId() {
		return StackId;
	}

	public void setStackId(String stackId) {
		StackId = stackId;
	}

	public String getGitUrl() {
		return GitUrl;
	}

	public void setGitUrl(String GitUrl) {
		this.GitUrl = GitUrl;
	}

	public String getAppId() {
		return AppId;
	}

	public void setAppId(String appId) {
		AppId = appId;
	}

	public String getAppName() {
		return AppName;
	}

	public void setAppName(String appName) {
		AppName = appName;
	}

	public boolean isInternalRouting() {
		return InternalRouting;
	}

	public void setInternalRouting(boolean InternalRouting) {
		this.InternalRouting = InternalRouting;
	}

	public boolean isMaintenance() {
		return Maintenance;
	}

	public void setMaintenance(boolean maintenance) {
		Maintenance = maintenance;
	}

	public String getOwnerEmail() {
		return OwnerEmail;
	}

	public void setOwnerEmail(String ownerEmail) {
		OwnerEmail = ownerEmail;
	}

	public String getOwnerID() {
		return OwnerID;
	}

	public void setOwnerID(String ownerID) {
		OwnerID = ownerID;
	}

	public String getOrganizationID() {
		return OrganizationID;
	}

	public void setOrganizationID(String organizationID) {
		OrganizationID = organizationID;
	}

	public String getOrganizationName() {
		return OrganizationName;
	}

	public void setOrganizationName(String organizationName) {
		OrganizationName = organizationName;
	}

	public String getTeamID() {
		return TeamID;
	}

	public void setTeamID(String teamID) {
		TeamID = teamID;
	}

	public String getTeamName() {
		return TeamName;
	}

	public void setTeamName(String teamName) {
		TeamName = teamName;
	}

	public String getRegionID() {
		return RegionID;
	}

	public void setRegionID(String regionID) {
		RegionID = regionID;
	}

	public String getRegionName() {
		return RegionName;
	}

	public void setRegionName(String regionName) {
		RegionName = regionName;
	}

	public String getSpaceID() {
		return SpaceID;
	}

	public void setSpaceID(String spaceID) {
		SpaceID = spaceID;
	}

	public String getSpaceName() {
		return SpaceName;
	}

	public void setSpaceName(String spaceName) {
		SpaceName = spaceName;
	}

	public boolean isSpaceShield() {
		return SpaceShield;
	}

	public void setSpaceShield(boolean spaceShield) {
		SpaceShield = spaceShield;
	}

	public String getWebUrl() {
		return WebUrl;
	}

	public void setWebUrl(String WebUrl) {
		this.WebUrl = WebUrl;
	}

	public String getBuildpack() {
		return Buildpack;
	}
	public void setBuildpack(String buildpack) {
		Buildpack = buildpack;
	}
	
	public ArrayList<String[]> getAddons() {
		return (ArrayList<String[]>) Addons;
	}
	
	public void setAddons(List<String[]> addons) {
		Addons = (ArrayList<String[]>) addons;
	}
	
	public boolean checkCategoryViolation() {
		return true;
	}

	public ArrayList<String> getDeployStatus() {
		return deployStatus;
	}

	public void setDeployStatus(ArrayList<String> deployStatus) {
		this.deployStatus = deployStatus;
	}



	
}
