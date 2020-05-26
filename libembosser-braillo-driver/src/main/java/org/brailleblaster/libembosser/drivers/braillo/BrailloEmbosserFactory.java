package org.brailleblaster.libembosser.drivers.braillo;

import java.math.BigDecimal;
import java.util.List;
import org.brailleblaster.libembosser.spi.Embosser;
import org.brailleblaster.libembosser.spi.EmbosserFactory;
import org.brailleblaster.libembosser.spi.Rectangle;
import com.google.common.collect.ImmutableList;

public class BrailloEmbosserFactory implements EmbosserFactory {
	private final List<Embosser> embossers;
	public BrailloEmbosserFactory() {
		ImmutableList.Builder<Embosser> eb = new ImmutableList.Builder<Embosser>();
		eb.add(new Braillo200Embosser("libembosser.braillo.200", "Braillo 200", new Rectangle(BigDecimal.valueOf(330.0), BigDecimal.valueOf(356)), new Rectangle(BigDecimal.valueOf(140.0), BigDecimal.valueOf(102)), true));
		eb.add(new Braillo200Embosser("libembosser.braillo.400s", "Braillo 400S", new Rectangle(BigDecimal.valueOf(330.0), BigDecimal.valueOf(356)), new Rectangle(BigDecimal.valueOf(140.0), BigDecimal.valueOf(102)), true));
		eb.add(new Braillo200Embosser("libembosser.braillo.400sr", "Braillo 400SR", new Rectangle(BigDecimal.valueOf(330.0), BigDecimal.valueOf(356)), new Rectangle(BigDecimal.valueOf(140.0), BigDecimal.valueOf(102)), true));
		eb.add(new Braillo200Embosser("libembosser.braillo.600", "Braillo 600", new Rectangle(BigDecimal.valueOf(330.0), BigDecimal.valueOf(356)), new Rectangle(BigDecimal.valueOf(140.0), BigDecimal.valueOf(102)), true));
		eb.add(new Braillo200Embosser("libembosser.braillo.600sr", "Braillo 600SR", new Rectangle(BigDecimal.valueOf(330.0), BigDecimal.valueOf(356)), new Rectangle(BigDecimal.valueOf(140.0), BigDecimal.valueOf(102)), true));
		eb.add(new Braillo270Embosser("libembosser.braillo.270v12", "Braillo 270", Braillo270DocumentHandler.Firmware.V12_16, new Rectangle("330.0", "356.0"), new Rectangle("165.0", "102.0"), true));
		embossers = eb.build();
	}

	@Override
	public List<Embosser> getEmbossers() {
		return embossers;
	}


}
