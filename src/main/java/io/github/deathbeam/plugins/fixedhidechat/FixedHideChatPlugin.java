package io.github.deathbeam.plugins.fixedhidechat;

import static io.github.deathbeam.plugins.fixedhidechat.FixedHideChatConstants.configGroup;
import static io.github.deathbeam.plugins.fixedhidechat.FixedHideChatConstants.AUTO_EXPAND_WIDGETS;
import static io.github.deathbeam.plugins.fixedhidechat.FixedHideChatConstants.SEED_VAULT_LIKE_POSITION_WIDGETS;
import static io.github.deathbeam.plugins.fixedhidechat.FixedHideChatConstants.DEFAULT_VIEW_HEIGHT;
import static io.github.deathbeam.plugins.fixedhidechat.FixedHideChatConstants.EXPANDED_VIEW_HEIGHT;
import static io.github.deathbeam.plugins.fixedhidechat.FixedHideChatConstants.BANK_X;
import static io.github.deathbeam.plugins.fixedhidechat.FixedHideChatConstants.BANK_Y;
import static io.github.deathbeam.plugins.fixedhidechat.FixedHideChatConstants.SEED_VAULT_X;
import static io.github.deathbeam.plugins.fixedhidechat.FixedHideChatConstants.CLAN_SETTINGS_X;
import static io.github.deathbeam.plugins.fixedhidechat.FixedHideChatConstants.CLAN_APPLICANTS_X;
import static io.github.deathbeam.plugins.fixedhidechat.FixedHideChatConstants.CLAN_BANS_X;
import static io.github.deathbeam.plugins.fixedhidechat.FixedHideChatConstants.DEFAULT_VIEW_BANK_WIDGET_HEIGHT;
import static io.github.deathbeam.plugins.fixedhidechat.FixedHideChatConstants.EXPANDED_VIEW_BANK_WIDGET_HEIGHT;
import static io.github.deathbeam.plugins.fixedhidechat.FixedHideChatConstants.DEFAULT_VIEW_POLL_WIDGET_HEIGHT;
import static io.github.deathbeam.plugins.fixedhidechat.FixedHideChatConstants.EXPANDED_VIEW_POLL_WIDGET_HEIGHT;
import static io.github.deathbeam.plugins.fixedhidechat.FixedHideChatConstants.FIXED_VIEWPORT_CLAN_SETTINGS;
import static io.github.deathbeam.plugins.fixedhidechat.FixedHideChatConstants.FIXED_VIEWPORT_CLAN_APPLICANTS;
import static io.github.deathbeam.plugins.fixedhidechat.FixedHideChatConstants.FIXED_VIEWPORT_CLAN_BANS;
import static io.github.deathbeam.plugins.fixedhidechat.FixedHideChatConstants.FIXED_MAIN;
import static io.github.deathbeam.plugins.fixedhidechat.FixedHideChatConstants.TO_CONTRACT_BANK_WIDGETS;
import static io.github.deathbeam.plugins.fixedhidechat.FixedHideChatConstants.TO_CONTRACT_POLL_WIDGETS;
import java.awt.event.KeyEvent;
import java.util.*;
import javax.inject.Inject;

import com.google.inject.*;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.BeforeRender;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.widgets.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.*;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.*;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Fixed Mode Hide Chat",
	description = "Hides chatbox in fixed mode and expands the view"
)
public class FixedHideChatPlugin extends Plugin implements KeyListener
{
	@Inject
	private Client client;

	@Inject
	private FixedHideChatConfig config;

	@Inject
	private ClientThread clientThread;

	@Inject
	private KeyManager keyManager;

	private int lastMenu = 0;
	private boolean hideChat = true;
	private boolean hideChatPrevious = hideChat;
	private boolean resizeViewport;
	private Keybind hideChatHotkey;

	@Override
	protected void startUp() throws Exception
	{
		updateConfig();
		// Register listener
		keyManager.registerKeyListener(this);
	}

	@Override
	protected void shutDown() throws Exception
	{
		// Unregister listener
		keyManager.unregisterKeyListener(this);

		// Reset menu state
		hideChat = true;
		lastMenu = 0;

		// Reset widgets
		clientThread.invoke(this::resetWidgets);
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
		keyReleased(e);
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		keyReleased(e);
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		if (!client.isResized() && !hideChat && e.getKeyCode() == hideChatHotkey.getKeyCode() && e.getModifiersEx() == hideChatHotkey.getModifiers())
		{
			hideChat = true;
			e.consume();
		}
	}

	@Subscribe
	public void onConfigChanged(final ConfigChanged event)
	{
		if (event.getGroup().equals(configGroup)) {
			updateConfig();
		}
	}

	@Subscribe
	public void onBeforeRender(final BeforeRender event)
	{
		if (client.isResized())
		{
			return;
		}

		// Bank container sometimes moves offscreen on resize and quick inputs, workaround
		final Widget bankWidget = client.getWidget(ComponentID.BANK_CONTAINER);
		if (bankWidget != null && !bankWidget.isSelfHidden())
		{
			// call [clientscript,bankmain_init] because otherwise the tag tabs don't extend properly
			// but don't call it every frame because then performance tanks
			// Causes a very slight flicker of the tag tab above the swap button sadly when opening the bank without the chat hidden
			if (hideChatPrevious != hideChat)
			{
				client.createScriptEvent(bankWidget.getOnLoadListener())
						.setSource(bankWidget)
						.run();
			}
			changeWidgetXY(bankWidget, BANK_X);
		}

		// A couple interfaces like to move offscreen on resize and quick inputs or are just positioned incorrectly
		changeSeedVaultLikePositionWidgetXY();
		changeWidgetXYConstants(FIXED_VIEWPORT_CLAN_SETTINGS, CLAN_SETTINGS_X);
		changeWidgetXYConstants(FIXED_VIEWPORT_CLAN_APPLICANTS, CLAN_APPLICANTS_X);
		changeWidgetXYConstants(FIXED_VIEWPORT_CLAN_BANS, CLAN_BANS_X);

		if (!hideChat && resizeViewport)
		{
			setViewSizeTo(EXPANDED_VIEW_HEIGHT, DEFAULT_VIEW_HEIGHT);
		} else	{
			// Expand the view height
			setViewSizeTo(DEFAULT_VIEW_HEIGHT, EXPANDED_VIEW_HEIGHT);
		}

		final Widget chatboxMessages = client.getWidget(ComponentID.CHATBOX_FRAME);

		if (chatboxMessages != null)
		{
			boolean found = !hideChat;

			// Check if any auto-expand interface is open
			if (!found)
			{
				for (final Map.Entry<Integer, Integer> widgets : AUTO_EXPAND_WIDGETS)
				{
					final Widget widget = widgets.getValue() == 0 ? client.getWidget(widgets.getKey()) : client.getWidget(widgets.getKey(), widgets.getValue());

					if (widget != null && !widget.isSelfHidden())
					{
						found = true;
						break;
					}
				}
			}

			// Resize some widgets that might interfere with having expanded chat
			setWidgetsSizeTo(
				found ? EXPANDED_VIEW_BANK_WIDGET_HEIGHT : DEFAULT_VIEW_BANK_WIDGET_HEIGHT,
				found ? DEFAULT_VIEW_BANK_WIDGET_HEIGHT : EXPANDED_VIEW_BANK_WIDGET_HEIGHT,
				TO_CONTRACT_BANK_WIDGETS);
			setWidgetsSizeTo(
				found ? EXPANDED_VIEW_POLL_WIDGET_HEIGHT : DEFAULT_VIEW_POLL_WIDGET_HEIGHT,
				found ? DEFAULT_VIEW_POLL_WIDGET_HEIGHT : EXPANDED_VIEW_POLL_WIDGET_HEIGHT,
				TO_CONTRACT_POLL_WIDGETS);

			// Hide/show chat messages
			chatboxMessages.setHidden(!found);
		}

		hideChatPrevious = hideChat;
	}

	@Subscribe
	public void onMenuOptionClicked(final MenuOptionClicked event)
	{
		if (!"Switch tab".equals(event.getMenuOption()))
		{
			return;
		}

		final Widget chatboxMessages = client.getWidget(ComponentID.CHATBOX_FRAME);
		final int newMenu = event.getParam1(); // Param1 is the same as getWidget().getId()
		hideChat = true;

		if (newMenu != lastMenu || (chatboxMessages != null && chatboxMessages.isHidden()))
		{
			hideChat = false;
			lastMenu = newMenu;
		}
	}

	private void updateConfig()
	{
		resizeViewport = config.resizeViewport();
		hideChatHotkey = config.hideChatHotkey();
	}

	private void changeSeedVaultLikePositionWidgetXY()
	{
		for (final Map.Entry<Integer, Integer> widget : SEED_VAULT_LIKE_POSITION_WIDGETS)
		{
			changeWidgetXYConstants(widget, SEED_VAULT_X);
		}
	}

	private void changeWidgetXYConstants(Map.Entry<Integer, Integer> widgetConstant, int xPosition)
	{
		final Widget widget = widgetConstant.getValue() == 0 ? client.getWidget(widgetConstant.getKey()) : client.getWidget(widgetConstant.getKey(), widgetConstant.getValue());

		if (widget != null && !widget.isSelfHidden())
		{
			changeWidgetXY(widget, xPosition);
		}
	}

	private static void changeWidgetXY(final Widget widget, int xPosition)
	{
		widget.setOriginalX(xPosition);
		widget.setOriginalY(BANK_Y);
		widget.setXPositionMode(WidgetPositionMode.ABSOLUTE_LEFT);
		widget.setYPositionMode(WidgetPositionMode.ABSOLUTE_TOP);
		widget.revalidateScroll();
	}

	private static void setWidgetHeight(final Widget widget, final int height)
    {
			widget.setOriginalHeight(height);
			widget.setHeightMode(WidgetSizeMode.ABSOLUTE);
			widget.revalidateScroll();
    }

	private static void changeWidgetHeight(int originalHeight, int newHeight, Widget widget)
	{
		if (widget.getHeight() == originalHeight)
		{
			setWidgetHeight(widget, newHeight);
			final Widget[] nestedChildren = widget.getNestedChildren();

			if (nestedChildren != null)
			{
				for (final Widget nestedChild : nestedChildren)
				{
					if (nestedChild.getHeight() == originalHeight)
					{
						setWidgetHeight(nestedChild, newHeight);
					}
				}
			}

			final Widget[] dynamicChildren = widget.getDynamicChildren();

			if (dynamicChildren != null)
			{
				for (final Widget child : dynamicChildren)
				{
					if (child.getHeight() == originalHeight)
					{
						setWidgetHeight(child, newHeight);
					}
				}
			}
		}
	}

	private void setWidgetsSizeTo(final int originalHeight, final int newHeight, Set<Map.Entry<Integer, Integer>> widgetsMap)
	{
		for (final Map.Entry<Integer, Integer> widgets : widgetsMap)
		{
			final Widget widget = widgets.getValue() == 0 ? client.getWidget(widgets.getKey()) : client.getWidget(widgets.getKey(), widgets.getValue());
			if (widget != null && !widget.isSelfHidden())
			{
				changeWidgetHeight(originalHeight, newHeight, widget);
			}
		}
	}

	private void setViewSizeTo(final int originalHeight, final int newHeight)
	{
		final Widget viewport = client.getWidget(ComponentID.FIXED_VIEWPORT_FIXED_VIEWPORT);

		if (viewport != null)
		{
			setWidgetHeight(viewport, newHeight);
		}

		final Widget fixedMain = client.getWidget(FIXED_MAIN.getKey(), FIXED_MAIN.getValue());

		if (fixedMain != null && fixedMain.getHeight() == originalHeight)
		{
			setWidgetHeight(fixedMain, newHeight);

			final Widget[] staticChildren = fixedMain.getStaticChildren();

			// Expand all children of the main fixed view
			for (final Widget child : staticChildren)
			{
				changeWidgetHeight(originalHeight, newHeight, child);
			}
		}

	}

	private void resetWidgets()
	{
		if (client.isResized())
		{
			return;
		}

		// Contract the view if it is expanded
		setViewSizeTo(EXPANDED_VIEW_HEIGHT, DEFAULT_VIEW_HEIGHT);
		setWidgetsSizeTo(EXPANDED_VIEW_BANK_WIDGET_HEIGHT, DEFAULT_VIEW_BANK_WIDGET_HEIGHT, TO_CONTRACT_BANK_WIDGETS);
		setWidgetsSizeTo(EXPANDED_VIEW_POLL_WIDGET_HEIGHT, DEFAULT_VIEW_POLL_WIDGET_HEIGHT, TO_CONTRACT_POLL_WIDGETS);

		// Show the chat messages widget again
		final Widget chatboxMessages = client.getWidget(ComponentID.CHATBOX_FRAME);

		if (chatboxMessages != null)
		{
			chatboxMessages.setHidden(false);
		}
	}

	@Provides
	FixedHideChatConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(FixedHideChatConfig.class);
	}
}
