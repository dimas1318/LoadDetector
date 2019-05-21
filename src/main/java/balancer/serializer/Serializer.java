package balancer.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.ClosureSerializer;
import continuation.StatedContinuation;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.invoke.SerializedLambda;
import java.nio.file.Files;
import java.nio.file.Path;

public class Serializer {

    private final KryoContext kryoContext;

    public Serializer() {
        kryoContext = DefaultKryoContext.newKryoContextFactory(kryo -> {
            kryo.register(StatedContinuation.class);
            kryo.register(Continuation.class);
            kryo.register(ContinuationScope.class);
            kryo.register(Object[].class);
            kryo.register(Class.class);
            kryo.register(SerializedLambda.class);
            kryo.register(ClosureSerializer.Closure.class, new ClosureSerializer());
            kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        });
    }

    public void serialize(StatedContinuation continuation, String fileName) {
        try {
            kryoContext.serialize(continuation, fileName);
        } catch (FileNotFoundException e) {
            System.err.println("serialize: Not found " + fileName);
        }
    }

    public StatedContinuation deserialize(String fileName) {
        try {
            StatedContinuation continuation = (StatedContinuation) kryoContext.deserialize(fileName);
            Files.deleteIfExists(Path.of(fileName));
            return continuation;
        } catch (IOException e) {
            System.err.println("deserialize: Not found " + fileName);
        }
        return null;
    }
}
