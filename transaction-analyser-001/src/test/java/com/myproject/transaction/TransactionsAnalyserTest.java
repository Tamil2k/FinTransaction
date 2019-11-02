package com.myproject.transaction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.assertj.core.data.Percentage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@RunWith(JUnit4.class)
public class TransactionsAnalyserTest {

	private static final String TEST_CSV_DATA_FILE_NAME = "test-transactions.csv";

	@Test
	public void main_MethodWithNoArguments_illegalArgumentException() throws Exception {
		assertThatThrownBy(() -> TransactionsAnalyser.main(new String[] {}))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Invalid number of arguments supplied");
	}

	@Test
	public void transactionsAnalyser_accountDoesNotExist_illegalArgumentException() throws Exception {
		final URL testCsvUrl = TransactionsAnalyser.class.getClassLoader().getResource(TEST_CSV_DATA_FILE_NAME);

		final TransactionsAnalysis transactionsAnalysis = new TransactionsAnalyser(Paths.get(testCsvUrl.toURI()))
				.analise("ACC000001", LocalDateTime.parse("20/10/2018 12:00:00", Transaction.DATE_TIME_FORMATTER),
						LocalDateTime.parse("20/10/2018 19:00:00", Transaction.DATE_TIME_FORMATTER));

		assertThat(transactionsAnalysis.getRelativeBalance()).isCloseTo(BigDecimal.ZERO, Percentage.withPercentage(3));
		assertThat(transactionsAnalysis.getPaymentTransactionsInRange()).isEmpty();
	}

	@Test
	public void main_accountHasReverseTransactions_success() throws Exception {
		final URL testCsvUrl = TransactionsAnalyser.class.getClassLoader().getResource(TEST_CSV_DATA_FILE_NAME);

		final TransactionsAnalysis transactionsAnalysis = new TransactionsAnalyser(Paths.get(testCsvUrl.toURI()))
				.analise("ACC334455", LocalDateTime.parse("20/10/2018 12:00:00", Transaction.DATE_TIME_FORMATTER),
						LocalDateTime.parse("20/10/2018 19:00:00", Transaction.DATE_TIME_FORMATTER));

		assertThat(transactionsAnalysis.getRelativeBalance()).isCloseTo(new BigDecimal(-25),
				Percentage.withPercentage(3));
		assertThat(transactionsAnalysis.getPaymentTransactionsInRange()).hasSize(1);
	}

	@Test
	public void main_accountDoesNotHaveReverseTransactions_success() throws Exception {
		final URL testCsvUrl = TransactionsAnalyser.class.getClassLoader().getResource(TEST_CSV_DATA_FILE_NAME);

		final TransactionsAnalysis transactionsAnalysis = new TransactionsAnalyser(Paths.get(testCsvUrl.toURI()))
				.analise("ACC778899", LocalDateTime.parse("20/10/2018 12:00:00", Transaction.DATE_TIME_FORMATTER),
						LocalDateTime.parse("20/10/2018 19:00:00", Transaction.DATE_TIME_FORMATTER));

		assertThat(transactionsAnalysis.getRelativeBalance()).isCloseTo(new BigDecimal(30),
				Percentage.withPercentage(3));
		assertThat(transactionsAnalysis.getPaymentTransactionsInRange()).hasSize(2);
	}
}
