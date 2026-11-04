package obs07.chroma.forge1201;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

// Logo, title, wrapped text and up to three buttons (SRG names, see stubs/forge1201)
final class ChromaScreen extends Screen {

	static final ResourceLocation LOGO = new ResourceLocation("chromapatch", "textures/gui/logo.png");

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
		super(Component.m_237113_(title));
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
		return Math.max(24, Math.min(64, f_96544_ / 6));
	}

	int textTop() {
		return 8 + logoHeight() + 20;
	}

	@Override
	protected void m_7856_() {
		lines = f_96547_.m_92923_(Component.m_237113_(message), Math.min(f_96543_ - 40, 440));
		int y = f_96544_ - 28;
		int buttons = (no != null ? 2 : 1) + (url != null ? 1 : 0);
		int w = buttons == 1 ? 200 : buttons == 2 ? 150 : 110;
		int x = f_96543_ / 2 - (buttons * w + (buttons - 1) * 8) / 2;
		if (url != null) {
			final Screen self = this;
			m_142416_(Button.m_253074_(Component.m_237113_(linkLabel), b -> ConfirmLinkScreen.m_274480_(url, self, false)).m_252987_(x, y, w, 20).m_253136_());
			x += w + 8;
		}
		m_142416_(Button.m_253074_(Component.m_237113_(yes), b -> answer(true)).m_252987_(x, y, w, 20).m_253136_());
		if (no != null) m_142416_(Button.m_253074_(Component.m_237113_(no), b -> answer(false)).m_252987_(x + w + 8, y, w, 20).m_253136_());
	}

	@Override
	public void m_88315_(GuiGraphics g, int mouseX, int mouseY, float partial) {
		m_280273_(g);
		super.m_88315_(g, mouseX, mouseY, partial);
		int h = logoHeight();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		g.m_280163_(LOGO, f_96543_ / 2 - h, 8, 0, 0, h * 2, h, h * 2, h);
		RenderSystem.disableBlend();
		g.m_280137_(f_96547_, title, f_96543_ / 2, 8 + h + 6, 0xFFFFFF);
		int y = textTop();
		int max = Math.max(1, (f_96544_ - 28 - 6 - y) / 10);
		for (int i = 0; i < lines.size() && i < max; i++) {
			if (i == max - 1 && lines.size() > max) g.m_280137_(f_96547_, "...", f_96543_ / 2, y, 0xA0A0A0);
			else g.m_280364_(f_96547_, lines.get(i), f_96543_ / 2, y, 0xE0E0E0);
			y += 10;
		}
	}

	// Escape closes without an answer
	@Override
	public void m_7379_() {
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
