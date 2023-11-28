package gov.uk.ets.registry.api.config;

import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.spi.MetadataBuilderContributor;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

public class SqlFunctionsMetadataBuilderContributor
    implements
        MetadataBuilderContributor {

    @Override
    public void contribute(MetadataBuilder metadataBuilder) {
        metadataBuilder.applySqlFunction(
            "string_agg",
            new StandardSQLFunction(
                "string_agg",
                StandardBasicTypes.STRING
            )
        );

    }

}
