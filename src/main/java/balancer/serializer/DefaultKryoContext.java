package balancer.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class DefaultKryoContext implements KryoContext {

    private KryoPool pool;

    public static KryoContext newKryoContextFactory(KryoClassRegistrator registrator) {
        return new DefaultKryoContext(registrator);
    }

    private DefaultKryoContext(KryoClassRegistrator registrator) {
        KryoFactory factory = new KryoFactoryImpl(registrator);

        pool = new KryoPool.Builder(factory).softReferences().build();
    }

    private static class KryoFactoryImpl implements KryoFactory {

        private KryoClassRegistrator registrator;

        public KryoFactoryImpl(KryoClassRegistrator registrator) {
            this.registrator = registrator;
        }

        @Override
        public Kryo create() {
            Kryo kryo = new Kryo();
            registrator.register(kryo);

            return kryo;
        }
    }

    @Override
    public void serialize(Object obj, String fileName) throws FileNotFoundException {
        Kryo kryo = pool.borrow();

        Output output = new Output(new FileOutputStream(fileName));
        kryo.writeClassAndObject(output, obj);
        output.close();

        pool.release(kryo);
    }

    @Override
    public Object deserialize(String fileName) throws FileNotFoundException {
        Kryo kryo = pool.borrow();

        Input input = new Input(new FileInputStream(fileName));
        Object obj = kryo.readClassAndObject(input);
        input.close();

        pool.release(kryo);

        return obj;
    }

}