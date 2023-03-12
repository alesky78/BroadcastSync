package it.spaghettisource.broadcastsync.exception;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Locale;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.spaghettisource.broadcastsync.BroadCastSyncConfig;
import it.spaghettisource.broadcastsync.i18n.FileMessageHelper;
import it.spaghettisource.broadcastsync.i18n.FileMessageRepository;

public class ExceptionFactoryTest {

	private static Logger  log = LoggerFactory.getLogger(ExceptionFactoryTest.class);

	private ExceptionFactory buildExceptionFactory() {
		//prepare the i18n messaged
		FileMessageRepository exceptionMessageRepository = new FileMessageRepository();
		exceptionMessageRepository.setMessageRepositoryBundleBaseName("i18n.exception-message");
		FileMessageHelper exceptionMessageHelper = new FileMessageHelper();
		exceptionMessageHelper.setMessageRepository(exceptionMessageRepository);

		BroadCastSyncConfig config = BroadCastSyncConfig.buildDefault();
		
		//create the exception factory
		return new ExceptionFactory(config, exceptionMessageHelper);
	}

	@DisplayName("test_exceptiofactory_default_locale")
	@Test
	public void test_exceptiofactory_default_locale() {

		Locale.setDefault(Locale.ENGLISH);
		ExceptionFactory exceptionFactory = buildExceptionFactory();

		log.info((exceptionFactory.getBroadcastAddressCannotBeResolved(new UnknownHostException(), "123.123.123.123")).getLocalizedMessage());
		log.info((exceptionFactory.getBroadCastSynAlreadyStarted()).getLocalizedMessage());
		log.info((exceptionFactory.getBroadCastSyncExceptionDatagramDataProtocolNotRespected(new Exception())).getLocalizedMessage());
		log.info((exceptionFactory.getImpossibleDeserializeObject(new Exception())).getLocalizedMessage());
		log.info((exceptionFactory.getImpossibleOpenDatagramSocket(new SocketException(), 1234)).getLocalizedMessage());
		log.info((exceptionFactory.getImpossibleSerializeObject(new Exception())).getLocalizedMessage());
		log.info((exceptionFactory.getLocalHostNameCannotBeResolved(new UnknownHostException())).getLocalizedMessage());
		log.info((exceptionFactory.getUnexpectedException(new Throwable())).getLocalizedMessage());

	}
	
	
	@DisplayName("test_exceptiofactory_it_IT_locale")
	@Test
	public void test_exceptiofactory_it_IT_locale() {

		Locale.setDefault(new Locale("it", "IT"));
		ExceptionFactory exceptionFactory = buildExceptionFactory();

		log.info((exceptionFactory.getBroadcastAddressCannotBeResolved(new UnknownHostException(), "123.123.123.123")).getLocalizedMessage());
		log.info((exceptionFactory.getBroadCastSynAlreadyStarted()).getLocalizedMessage());
		log.info((exceptionFactory.getBroadCastSyncExceptionDatagramDataProtocolNotRespected(new Exception())).getLocalizedMessage());
		log.info((exceptionFactory.getImpossibleDeserializeObject(new Exception())).getLocalizedMessage());
		log.info((exceptionFactory.getImpossibleOpenDatagramSocket(new SocketException(), 1234)).getLocalizedMessage());
		log.info((exceptionFactory.getImpossibleSerializeObject(new Exception())).getLocalizedMessage());
		log.info((exceptionFactory.getLocalHostNameCannotBeResolved(new UnknownHostException())).getLocalizedMessage());
		log.info((exceptionFactory.getUnexpectedException(new Throwable())).getLocalizedMessage());

	}
	

}
