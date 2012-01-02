package samson.jersey;

import java.lang.reflect.Type;

import javax.ws.rs.core.Context;

import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.spi.inject.ConstrainedTo;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;
import com.sun.jersey.spi.inject.ServerSide;

@ConstrainedTo(ServerSide.class)
public class MetadataCacheInjectableProvider implements InjectableProvider<Context, Type> {

    private final MetadataCache cache;

    public MetadataCacheInjectableProvider() {
        this.cache = new MetadataCache();
    }

    @Override
    public ComponentScope getScope() {
        return ComponentScope.Singleton;
    }

    @Override
    public Injectable<MetadataCache> getInjectable(ComponentContext componentContext, Context annotation, Type type) {
        if (MetadataCache.class != type) {
            return null;
        }

        return new Injectable<MetadataCache>() {

            @Override
            public MetadataCache getValue() {
                return cache;
            }
        };
    }

}
