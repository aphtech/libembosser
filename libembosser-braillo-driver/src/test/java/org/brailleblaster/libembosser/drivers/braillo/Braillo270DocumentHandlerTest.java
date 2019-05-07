package org.brailleblaster.libembosser.drivers.braillo;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.testng.annotations.Test;

import com.google.common.base.Charsets;

public class Braillo270DocumentHandlerTest {
	@Test
	public void testDefaultHeader() throws IOException {
		Braillo270DocumentHandler handler = new Braillo270DocumentHandler.Builder().build();
		String header = handler.getHeader().asCharSource(Charsets.US_ASCII).read();
		assertThat(header).contains("\u001bE", "\u001bA", "\u001b6");
	}
}
