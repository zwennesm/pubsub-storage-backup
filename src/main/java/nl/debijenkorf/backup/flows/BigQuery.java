package nl.debijenkorf.backup.flows;

import lombok.Builder;
import nl.debijenkorf.backup.functions.StringToTableRow;
import nl.debijenkorf.backup.parsers.RowParser;
import nl.debijenkorf.backup.parsers.SchemeParser;
import nl.debijenkorf.backup.values.Field;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;

import java.util.List;

@Builder
public class BigQuery {

    private PCollection<String> source;
    private String table;
    private List<Field> fields;
    private String separator;
    @Builder.Default private RowParser parser = new RowParser();

    public void write() {
        source.apply("Convert msg to Table row",
                ParDo.of(new StringToTableRow(separator, fields, parser)))
                .apply(BigQueryIO.writeTableRows()
                        .to(table)
                        .withCreateDisposition(BigQueryIO.Write.CreateDisposition.CREATE_IF_NEEDED)
                        .withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_APPEND)
                        .withSchema(SchemeParser.toTableSchema(fields)));
    }

}
