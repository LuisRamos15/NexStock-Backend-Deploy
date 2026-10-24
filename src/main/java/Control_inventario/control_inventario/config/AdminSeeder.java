package Control_inventario.control_inventario.config;

import Control_inventario.control_inventario.entidad.Rol;
import Control_inventario.control_inventario.entidad.Usuario;
import Control_inventario.control_inventario.repositorio.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

@Configuration
public class AdminSeeder {

    @Value("${app.admin.username:admin}")
    private String nombreUsuario;

    @Value("${app.admin.password:}")
    private String passwordPlano;

    @Bean
    CommandLineRunner initAdmin(
            UsuarioRepositorio usuarioRepositorio,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {

            Optional<Usuario> existente =
                    usuarioRepositorio.findByNombreUsuario(nombreUsuario);

            if (existente.isPresent()) {

                Usuario admin = existente.get();
                boolean actualizado = false;

                if (admin.getRoles() == null ||
                        !admin.getRoles().contains(Rol.SUPER_ADMIN)) {

                    admin.setRoles(Set.of(Rol.SUPER_ADMIN));
                    actualizado = true;
                }

                if (!admin.isActivo()) {
                    admin.setActivo(true);
                    actualizado = true;
                }

                if (actualizado) {
                    usuarioRepositorio.save(admin);
                    System.out.println(
                            "Usuario administrador actualizado correctamente"
                    );
                }

            } else {

                if (passwordPlano == null || passwordPlano.isBlank()) {
                    System.out.println(
                            "No se creó el administrador inicial porque no se configuró ADMIN_INITIAL_PASSWORD."
                    );
                    return;
                }

                Usuario admin = new Usuario();

                admin.setNombreUsuario(nombreUsuario);
                admin.setPasswordHash(
                        passwordEncoder.encode(passwordPlano)
                );
                admin.setRoles(Set.of(Rol.SUPER_ADMIN));
                admin.setActivo(true);

                usuarioRepositorio.save(admin);

                System.out.println(
                        "Usuario SUPER_ADMIN inicial creado correctamente"
                );
            }
        };
    }
}