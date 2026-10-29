package co.edu.comfenalco.citas.repositorio;
import co.edu.comfenalco.citas.modelo.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
public interface CitaRepository extends JpaRepository<Cita,Long> {
 boolean existsByProfesionalAndFechaHora(String profesional,LocalDateTime fechaHora);
 long countByProfesionalAndFechaHoraBetween(String profesional,LocalDateTime inicio,LocalDateTime fin);
}
