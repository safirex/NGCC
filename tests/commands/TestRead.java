package commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.qos.logback.classic.Level;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "TestRead", version = "Version 1.0", sortOptions = false, usageHelpWidth = 60, header = "\n ----  Nicely Generated and Corrected Copies  ----\n\n", footer = "\n\n  ---- Provided by IUT Info Nice S3T-G4 ---- \n", description = "")

class TestRead {

	Read read = new Read(System.out);

	@BeforeEach
	void setUp() {

		this.read = new Read(System.out);

		CommandLine cmd = new CommandLine(new TestRead()).addSubcommand("-r", this.read);

		String[] t = { "-r", "-v9", "-d", "pdf", "config/source.txt" };
		cmd.execute(t);
	}

	@Test
	void testCommand() {

		assertFalse(this.read == null);

		assertEquals("pdf", this.read.directory_name);
		assertEquals(false, this.read.help);
		assertEquals(9, this.read.vb_level);
		assertEquals("config/source.txt", this.read.source_path);
		assertEquals("result.csv", this.read.result_name);

		assertFalse(this.read.logger == null);
		assertEquals(Level.DEBUG.toString(), this.read.logger.getLevel().toString());
	}

}
