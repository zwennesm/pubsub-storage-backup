package nl.debijenkorf.backup.flows;

import lombok.Builder;
import nl.debijenkorf.backup.utils.WindowedFilenamePolicy;
import org.apache.beam.sdk.io.Compression;
import org.apache.beam.sdk.io.FileBasedSink;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.fs.ResourceId;
import org.apache.beam.sdk.options.ValueProvider;
import org.apache.beam.sdk.transforms.SerializableFunction;
import org.apache.beam.sdk.values.PCollection;

@Builder
public class Storage {

    private int shards;
    private PCollection<String> source;
    private WindowedFilenamePolicy filePattern;

    public void write() {
        source.apply(TextIO.write()
                .to(filePattern)
                .withCompression(Compression.GZIP)
                .withWindowedWrites()
                .withNumShards(shards)
                .withTempDirectory(ValueProvider.NestedValueProvider.of(
                        filePattern.getOutputDirectory(),
                        (SerializableFunction<String, ResourceId>) FileBasedSink::convertToFileResourceIfPossible)));
    }

}
