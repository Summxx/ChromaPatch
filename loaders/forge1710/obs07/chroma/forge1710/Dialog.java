package obs07.chroma.forge1710;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import obs07.chroma.update.Links;
import org.lwjgl.opengl.GL11;

// Logo, title, wrapped text and up to three buttons.
// SRG: initGui func_73866_w_, drawScreen func_73863_a, actionPerformed func_146284_a, keyTyped func_73869_a,
// drawDefaultBackground func_146276_q_, drawCenteredString func_73732_a, listFormattedStringToWidth func_78271_c,
// buttonList field_146292_n, width field_146294_l, height field_146295_m, fontRendererObj field_146289_q, button id field_146127_k,
// getTextureManager func_110434_K, bindTexture func_110577_a, drawModalRectWithCustomSizedTexture func_146110_a
public final class Dialog extends GuiScreen {

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
	List<String> lines = new ArrayList<String>();
	boolean answered;

	Dialog(String title, String message, String yes, String no, String linkLabel, String url, Runnable onYes, Runnable onNo, Runnable onEscape) {
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
		return Math.max(24, Math.min(64, field_146295_m / 6));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void func_73866_w_() {
		field_146292_n.clear();
		lines = new ArrayList<String>();
		for (String part : message.split("\n")) {
			if (part.isEmpty()) lines.add("");
			else lines.addAll(field_146289_q.func_78271_c(part, Math.min(field_146294_l - 40, 440)));
		}
		int y = field_146295_m - 28;
		int buttons = (no != null ? 2 : 1) + (url != null ? 1 : 0);
		int w = buttons == 1 ? 200 : buttons == 2 ? 150 : 110;
		int x = field_146294_l / 2 - (buttons * w + (buttons - 1) * 8) / 2;
		if (url != null) {
			field_146292_n.add(new GuiButton(2, x, y, w, 20, linkLabel));
			x += w + 8;
		}
		field_146292_n.add(new GuiButton(0, x, y, w, 20, yes));
		if (no != null) field_146292_n.add(new GuiButton(1, x + w + 8, y, w, 20, no));
	}

	@Override
	public void func_73863_a(int mouseX, int mouseY, float partial) {
		func_146276_q_();
		int h = logoHeight();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(1, 1, 1, 1);
		Minecraft.func_71410_x().func_110434_K().func_110577_a(LOGO);
		Gui.func_146110_a(field_146294_l / 2 - h, 8, 0, 0, h * 2, h, h * 2, h);
		GL11.glDisable(GL11.GL_BLEND);
		func_73732_a(field_146289_q, title, field_146294_l / 2, 8 + h + 6, 0xFFFFFF);
		int y = 8 + h + 20;
		int max = Math.max(1, (field_146295_m - 28 - 6 - y) / 10);
		for (int i = 0; i < lines.size() && i < max; i++) {
			boolean cut = i == max - 1 && lines.size() > max;
			func_73732_a(field_146289_q, cut ? "..." : lines.get(i), field_146294_l / 2, y, cut ? 0xA0A0A0 : 0xE0E0E0);
			y += 10;
		}
		super.func_73863_a(mouseX, mouseY, partial);
	}

	@Override
	protected void func_146284_a(GuiButton button) {
		if (button.field_146127_k == 2) {
			final Dialog self = this;
			Runnable back = new Runnable() {
				public void run() {
					Client.show(self);
				}
			};
			Client.show(new Dialog("Open this link in your browser?", url, "Open", "Cancel", null, null, new Runnable() {
				public void run() {
					Links.open(url);
					Client.show(self);
				}
			}, back, back));
			return;
		}
		answer(button.field_146127_k == 0);
	}

	// Escape closes without an answer
	@Override
	protected void func_73869_a(char c, int key) {
		if (key != 1 || answered) return;
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
