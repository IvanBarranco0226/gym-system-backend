package com.gymsystem.api.user;

import com.gymsystem.api.auth.dto.LoginRequest;
import com.gymsystem.api.security.EmailService;
import com.gymsystem.api.user.dto.EmployeeResponse;
import com.gymsystem.api.user.dto.RegisterEmployeeRequest;
import com.gymsystem.api.user.dto.RegisterUserRequest;
import com.gymsystem.api.user.dto.UpdateEmployeeRequest;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    UserService(EmailService emailService) {
        this.emailService = emailService;
    }

    public User registerNewUser(RegisterUserRequest request) {
        // Regla de negocio 1: Verificar que el email no este en uso
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado en el sistema.");
        }

        // Creamos la entidad real que irá a la base de datos
        User newUser = new User();
        newUser.setEmail(request.getEmail());

        // Regla de negocio 2: Encriptación obligatoria
        newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        newUser.setRoleId(request.getRoleId());

        return userRepository.save(newUser);
    }

    public User loginUser(LoginRequest request) {
        // 1. Buscamos al usuario. Si no existe, lanzamos error genérico por seguridad
        // (no dar pistas)
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        // 2. Comparamos la contraseña en texto plano con el hash de la BD
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        // Si pasa, devolvemos el usuario (EL controlador se encargará de ocultar el
        // hash)
        return user;
    }

    @Transactional
    public User registerEmployee(RegisterEmployeeRequest request) {

        // 1. Validamos que el correo no exista
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado en el sistema.");
        }

        // 2. Construimos el objeto User
        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setFirstName(request.getFirstName());
        newUser.setLastName(request.getLastName());
        newUser.setPhone(request.getPhone());
        newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        newUser.setRoleId(request.getRoleId());
        newUser.setNeedsPasswordChange(true);

        // 3. Construímos el objeto perfil datos laborales
        EmployeeProfile profile = new EmployeeProfile();
        profile.setNss(request.getNss());
        profile.setSalary(request.getSalary());
        profile.setShift(request.getShift());
        profile.setHireDate(request.getHireDate());

        // 4. Ensamblamos los objetos
        // Le decimos al perfil quién es su usuario, y el usuario quién es su perfil
        profile.setUser(newUser);
        newUser.setEmployeeProfile(profile);

        // 5. Guardamos todo con una sola instruccón
        // Gracias a CascadeType.ALL, al guardar el usuario se guarda el perfil
        // automáticamente
        User savedUser = userRepository.save(newUser);
        emailService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getFirstName(), request.getPassword());
        return savedUser;
    }

    public List<EmployeeResponse> getAllEmployees() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRoleId() == 2 || user.getRoleId() == 3)
                .map(user -> {
                    EmployeeResponse response = new EmployeeResponse();
                    response.setId(user.getId());
                    response.setEmail(user.getEmail());
                    response.setFirstName(user.getFirstName());
                    response.setLastName(user.getLastName());
                    response.setPhone(user.getPhone());
                    response.setRoleId(user.getRoleId());

                    if (user.getEmployeeProfile() != null) {
                        response.setNss(user.getEmployeeProfile().getNss());
                        response.setSalary(user.getEmployeeProfile().getSalary());
                        response.setShift(user.getEmployeeProfile().getShift());
                        response.setHireDate(user.getEmployeeProfile().getHireDate());
                    }
                    return response;
                }).collect(Collectors.toList());
    }

    // Borrar empleado ( El CascadeType.ALL borrará también su perfil)
    @Transactional
    public void deleteEmployee(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("El empleado no existe");
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public void updateEmployee(UUID id, UpdateEmployeeRequest request) {
        // 1. Buscamos al usuario
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // 2. Regla de negocio: Si está intentando cambiar el correo, verificamos
        if (!user.getEmail().equals(request.getEmail()) && 
            userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new RuntimeException("El correo ya está registrado por otro usuario");
            }
        // 3. Actualizamos los datos básicos
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setRoleId(request.getRoleId());
        user.setPhone(request.getPhone());

        // 4. Actualizamos el perfil laboral si existe
        if (user.getEmployeeProfile() != null) {
            user.getEmployeeProfile().setNss(request.getNss());
            user.getEmployeeProfile().setSalary(request.getSalary());
            user.getEmployeeProfile().setShift(request.getShift());
        }

        // 5. Guardamos los cambios
        userRepository.save(user);
    }

    @Transactional
    public void updateFirstPassword(String email, String newPassword) {
        // 1. Buscamos al usuario basado en el correo que extrajimos del Token
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // 2. Encriptamos la nueva contraseña
        user.setPasswordHash(passwordEncoder.encode(newPassword));

        // 3. Apagamos la bandera: ya no le pediremos cambiarla en el futuro
        user.setNeedsPasswordChange(false);

        // 4. Guardamos
        userRepository.save(user);
    }
}
