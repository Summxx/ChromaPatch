package obs07.chroma.neoforge1211;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

// Logo, title, wrapped text and up to three buttons
final class ChromaScreen extends Screen {

	static final ResourceLocation LOGO = ResourceLocation.fromNamespaceAndPath("chromapatch", "textures/gui/logo.png");

	final String title;
	final String message;
	final String yes;
	final String no;
	final String linkLabel;
	final String url;
	final Runnable onYes;
	final Runnable onNo;
	final Runnable onEscape;
	List<FormattedCharSequence> lines;
	boolean answered;

	ChromaScreen(String title, String message, String yes, String no, String linkLabel, String url, Runnable onYes, Runnable onNo, Runnable onEscape) {
		super(Component.literal(title));
		this.title = title;
		this.message = message;
		this.yes = yes;
		this.no = no;
		this.linkLabel = linkLabel;
		this.url = url;
		this.onYes = onYes;
		this.onNo = onNo;
		this.onEscape = onEscape;
	}

	int logoHeight() {
		return Math.max(24, Math.min(64, height / 6));
	}

	int textTop() {
		return 8 + logoHeight() + 20;
	}

	@Override
	protected void init() {
		lines = font.split(Component.literal(message), Math.min(width - 40, 440));
		int y = height - 28;
		int buttons = (no != null ? 2 : 1) + (url != null ? 1 : 0);
		int w = buttons == 1 ? 200 : buttons == 2 ? 150 : 110;
		int x = width / 2 - (buttons * w + (buttons - 1) * 8) / 2;
		if (url != null) {
			final Screen self = this;
			addRenderableWidget(Button.builder(Component.literal(linkLabel), b -> ConfirmLinkScreen.confirmLinkNow(self, url)).bounds(x, y, w, 20).build());
			x += w + 8;
		}
		addRenderableWidget(Button.builder(Component.literal(yes), b -> answer(true)).bounds(x, y, w, 20).build());
		if (no != null) addRenderableWidget(Button.builder(Component.literal(no), b -> answer(false)).bounds(x + w + 8, y, w, 20).build());
	}

	@Override
	public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
		super.render(g, mouseX, mouseY, partial);
		int h = logoHeight();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		g.blit(LOGO, width / 2 - h, 8, 0, 0, h * 2, h, h * 2, h);
		RenderSystem.disableBlend();
		g.drawCenteredString(font, title, width / 2, 8 + h + 6, 0xFFFFFF);
		int y = textTop();
		int max = Math.max(1, (height - 28 - 6 - y) / 10);
		for (int i = 0; i < lines.size() && i < max; i++) {
			if (i == max - 1 && lines.size() > max) g.drawCenteredString(font, "...", width / 2, y, 0xA0A0A0);
			else g.drawCenteredString(font, lines.get(i), width / 2, y, 0xE0E0E0);
			y += 10;
		}
	}

	// Escape closes without an answer
	@Override
	public void onClose() {
		if (answered) return;
		answered = true;
		if (onEscape != null) onEscape.run();
	}

	void answer(boolean value) {
		if (answered) return;
		answered = true;
		Runnable r = value ? onYes : onNo;
		if (r != null) r.run();
	}
}
