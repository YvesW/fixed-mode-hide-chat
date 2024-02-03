package io.github.deathbeam.plugins.fixedhidechat;

import com.google.common.collect.ImmutableSet;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

import net.runelite.api.widgets.*;

public class FixedHideChatConstants
{
	private static final Map.Entry<Integer, Integer>  CHATBOX_MESSAGES_DIALOG = new AbstractMap.SimpleEntry<>(
		InterfaceID.DIALOG_OPTION,
		0
	);

	// Wrong PIN popup, idk what else; S162.565 (ID: 10617397)
	private static final Map.Entry<Integer, Integer>  CHATBOX_MESSAGES_SPECIAL = new AbstractMap.SimpleEntry<>(
		InterfaceID.CHATBOX,
		565
	);

	private static final Map.Entry<Integer, Integer>  CHATBOX_MESSAGES_DIALOG_NPC = new AbstractMap.SimpleEntry<>(
		InterfaceID.DIALOG_NPC,
		0
	);

	private static final Map.Entry<Integer, Integer>  CHATBOX_MESSAGES_DIALOG_PLAYER = new AbstractMap.SimpleEntry<>(
		InterfaceID.DIALOG_PLAYER,
		0
	);

	private static final Map.Entry<Integer, Integer>  CHATBOX_MESSAGES_DIALOG_SPRITE = new AbstractMap.SimpleEntry<>(
		InterfaceID.DIALOG_SPRITE,
		0
	);

	private static final Map.Entry<Integer, Integer>  CHATBOX_MESSAGES_CONTAINER = new AbstractMap.SimpleEntry<>(
		ComponentID.CHATBOX_CONTAINER,
		0
	);

	private static final Map.Entry<Integer, Integer>  FIXED_VIEWPORT_BANK_POPUP_CONTAINER = new AbstractMap.SimpleEntry<>(
		ComponentID.BANK_CONTAINER,
		0
	);

	private static final Map.Entry<Integer, Integer>  FIXED_VIEWPORT_SEED_VAULT_INVENTORY_ITEM_CONTAINER = new AbstractMap.SimpleEntry<>(
		ComponentID.SEED_VAULT_INVENTORY_ITEM_CONTAINER,
		0
	);

	private static final Map.Entry<Integer, Integer>  FIXED_VIEWPORT_POLL_VOTE = new AbstractMap.SimpleEntry<>(
		345,
		1
	);

	private static final Map.Entry<Integer, Integer>  FIXED_VIEWPORT_POLL_HISTORY = new AbstractMap.SimpleEntry<>(
		310,
		1
	);

	private static final Map.Entry<Integer, Integer>  FIXED_VIEWPORT_CLAN_MEMBER_LIST = new AbstractMap.SimpleEntry<>(
		693,
		1
	);

	private static final Map.Entry<Integer, Integer>  FIXED_VIEWPORT_CLAN_PERMISSIONS = new AbstractMap.SimpleEntry<>(
		706,
		1
	);

	static final Map.Entry<Integer, Integer>  FIXED_VIEWPORT_CLAN_SETTINGS = new AbstractMap.SimpleEntry<>(
		690,
		1
	);

	static final Map.Entry<Integer, Integer>  FIXED_VIEWPORT_CLAN_APPLICANTS = new AbstractMap.SimpleEntry<>(
		699,
		1
	);

	static final Map.Entry<Integer, Integer>  FIXED_VIEWPORT_CLAN_BANS = new AbstractMap.SimpleEntry<>(
		689,
		1
	);

	static final Map.Entry<Integer, Integer>  FIXED_MAIN = new AbstractMap.SimpleEntry<>(
		InterfaceID.FIXED_VIEWPORT,
		9
	);

	static final String configGroup = "FixedHideChat";
	static final int DEFAULT_VIEW_HEIGHT = 334;
	static final int EXPANDED_VIEW_HEIGHT = 476;
	static final int BANK_X = 12;
	static final int BANK_Y = 2;
	static final int SEED_VAULT_X = 6;
	static final int CLAN_SETTINGS_X = 26;
	static final int CLAN_APPLICANTS_X = 38;
	static final int CLAN_BANS_X = 18;

	// This is the VIEW_HEIGHT minus the BANK_Y minus 1 since there is a gap of 1 pixel at the bottom without the plugin.
	static final int DEFAULT_VIEW_BANK_WIDGET_HEIGHT = DEFAULT_VIEW_HEIGHT - BANK_Y - 1;
	static final int EXPANDED_VIEW_BANK_WIDGET_HEIGHT = EXPANDED_VIEW_HEIGHT - BANK_Y - 1;
	static final int DEFAULT_VIEW_POLL_WIDGET_HEIGHT = 324;
	static final int EXPANDED_VIEW_POLL_WIDGET_HEIGHT = 466;

	static final Set<Map.Entry<Integer, Integer>> AUTO_EXPAND_WIDGETS = ImmutableSet
		.<Map.Entry<Integer, Integer>>builder()
		.add(CHATBOX_MESSAGES_DIALOG)
		.add(CHATBOX_MESSAGES_SPECIAL)
		.add(CHATBOX_MESSAGES_CONTAINER)
		.add(CHATBOX_MESSAGES_DIALOG_NPC)
		.add(CHATBOX_MESSAGES_DIALOG_PLAYER)
		.add(CHATBOX_MESSAGES_DIALOG_SPRITE)
		.build();

	// These widgets are DEFAULT_VIEW_BANK_WIDGET_HEIGHT
	static final Set<Map.Entry<Integer, Integer>> TO_CONTRACT_BANK_WIDGETS = ImmutableSet
		.<Map.Entry<Integer, Integer>>builder()
		.add(FIXED_VIEWPORT_BANK_POPUP_CONTAINER)
		.add(FIXED_VIEWPORT_SEED_VAULT_INVENTORY_ITEM_CONTAINER)
		.build();

	// These widgets are DEFAULT_VIEW_POLL_WIDGET_HEIGHT
	static final Set<Map.Entry<Integer, Integer>> TO_CONTRACT_POLL_WIDGETS = ImmutableSet
		.<Map.Entry<Integer, Integer>>builder()
		.add(FIXED_VIEWPORT_POLL_VOTE)
		.add(FIXED_VIEWPORT_POLL_HISTORY)
		.add(FIXED_VIEWPORT_CLAN_MEMBER_LIST)
		.add(FIXED_VIEWPORT_CLAN_PERMISSIONS)
		.build();

	// These widgets use the SEED_VAULT_X and BANK_Y position
	static final Set<Map.Entry<Integer, Integer>> SEED_VAULT_LIKE_POSITION_WIDGETS = ImmutableSet
		.<Map.Entry<Integer, Integer>>builder()
		.add(FIXED_VIEWPORT_SEED_VAULT_INVENTORY_ITEM_CONTAINER)
		.addAll(TO_CONTRACT_POLL_WIDGETS)
		.build();
}
