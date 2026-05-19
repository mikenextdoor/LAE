import kotlin.jvm.JvmClassMappingKt;
import org.example.classes.EstatisticaAbuso;
import reflection.AbstractClassFormatter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EstatisticaAbusoBaseLine extends AbstractClassFormatter<EstatisticaAbuso> {
    public EstatisticaAbusoBaseLine() {
        super(JvmClassMappingKt.getKotlinClass(EstatisticaAbuso.class));
    }

    @Override
    public List<EstatisticaAbuso> toClassFormatter(ResultSet rs) throws SQLException {
        List<EstatisticaAbuso> lista = new ArrayList<>();
        while (rs.next()) {
            lista.add(new EstatisticaAbuso(rs.getBoolean("EAbusiva"), rs.getInt("Total")));
        }

        return lista;
    }
}