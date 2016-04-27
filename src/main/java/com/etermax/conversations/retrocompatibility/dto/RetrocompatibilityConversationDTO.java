package com.etermax.conversations.retrocompatibility.dto;

import com.etermax.conversations.retrocompatibility.date.RetrocompatibilityDateParser;
import com.fasterxml.jackson.annotation.JsonProperty;
import dto.UserDTO;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class RetrocompatibilityConversationDTO {

	@JsonProperty("total")
	private Integer total;
	@JsonProperty("list")
	private List<RetrocompatibilityMessageDTO> list;
	@JsonProperty("more")
	private Boolean more;
	@JsonProperty("is_favorite")
	private Boolean isFavorite;
	@JsonProperty("is_blacklisted")
	private Boolean isBlacklisted;
	@JsonProperty("you_are_blacklisted")
	private Boolean youAreBlacklisted;
	@JsonProperty("last_access")
	private String lastAccess;
	@JsonProperty("opp_fcbk_id")
	private String opponentFacebookId;

	public RetrocompatibilityConversationDTO(List<RetrocompatibilityMessageDTO> messages, UserDTO userDTO) {
		this.total = messages.size();
		this.list = messages;
		this.more = Boolean.FALSE;

		Map<String, Object> social_interactions = getSocialInteractions(userDTO);
		Map<String, Object> facebook_info = getFacebookInfo(userDTO);
		Long lastActivity = getLastActivity(userDTO);

		this.isFavorite = (Boolean) social_interactions.get("is_favorite");
		this.isBlacklisted = (Boolean) social_interactions.get("is_blocked");
		this.youAreBlacklisted = (Boolean) social_interactions.get("you_are_blocked");
		if (facebook_info != null) {
			this.opponentFacebookId = (String) facebook_info.get("id");
		}
		this.lastAccess = RetrocompatibilityDateParser.parseDate(lastActivity);
	}

	public Integer getTotal() {
		return total;
	}

	public List<RetrocompatibilityMessageDTO> getList() {
		return list;
	}

	public Map<String, Object> getFacebookInfo(UserDTO userDTO) {
		return (Map<String, Object>) userDTO.getExtensions().get("facebook");
	}

	public Map<String, Object> getSocialInteractions(UserDTO userDTO) {
		return (Map<String, Object>) userDTO.getExtensions().get("social_interactions");
	}

	public Long getLastActivity(UserDTO userDTO) {
		try {
			Integer lastActivity = (Integer) userDTO.getExtensions().get("last_activity");
			return lastActivity.longValue() * 1000;
		} catch (Exception e) {
			return new Date().getTime();
		}
	}

}
