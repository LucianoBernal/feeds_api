package com.etermax.conversations.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

import java.io.FileReader;

public class AppInfoDTO {

	public static AppInfoDTO _instance = new AppInfoDTO();

	@JsonProperty("version")
	private String version;

	@JsonProperty("application")
	private String appName;

	private AppInfoDTO(){
		this.appName = "conversations";
		this.version = readVersionFromFile();
	}

	private String readVersionFromFile() {
		MavenXpp3Reader mvnReader = new MavenXpp3Reader();
		try (FileReader fileReader = new FileReader("pom.xml")) {
			Model model = mvnReader.read(fileReader);
			fileReader.close();
			return model.getVersion();
		} catch (Exception e) {
			return "INVALID VERSION";
		}
	}

	public static AppInfoDTO getInstance(){
		return _instance;
	}

	public String getVersion() {
		return version;
	}
}
