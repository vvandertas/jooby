package jooby;

import java.nio.charset.Charset;
import java.util.List;

import com.google.inject.Injector;

public interface RequestFactory {

  Request newRequest(Injector injector, MessageConverterSelector selector,
      List<MediaType> accept, MediaType contentType,
      Charset defaultCharset);

}
