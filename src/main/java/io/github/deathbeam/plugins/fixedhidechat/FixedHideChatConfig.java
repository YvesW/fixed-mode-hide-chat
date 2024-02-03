package io.github.deathbeam.plugins.fixedhidechat;

import net.runelite.client.config.*;
import java.awt.event.*;

@ConfigGroup("FixedHideChat")
public interface FixedHideChatConfig extends Config
{
	@ConfigItem(
			position = 0,
			keyName = "resizeViewport",
			name = "Resize viewport",
			description = "Resize viewport when opening/closing chat"
	)
	default boolean resizeViewport()
	{
		return false;
	}

	@ConfigItem(
		keyName = "hideChatHotkey",
		name = "Hide chat hotkey",
		description = "Hotkey used to hide the chat.<br>"
			+ "Can be a combination of keys (e.g. ctrl+L). Set the key to 'Not set' to disable this setting.",
		position = 1
	)
	default Keybind hideChatHotkey() {
		return new Keybind(KeyEvent.VK_ESCAPE, 0);
	}

	@ConfigItem(
		keyName = "showChatHotkey",
		name = "Show chat hotkey",
		description = "Hotkey used to show the chat. Can be the same as the hotkey used to hide the chat, in case you want to toggle the chat.<br>"
			+ "Can be a combination of keys (e.g. ctrl+L). Set the key to 'Not set' to disable this setting.",
		position = 2
	)
	default Keybind showChatHotkey() {
		return Keybind.NOT_SET;
	}
}
