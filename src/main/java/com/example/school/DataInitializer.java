package com.example.school;

import com.example.school.entity.AppSettings;
import com.example.school.entity.City;
import com.example.school.entity.ModuleCategory;
import com.example.school.entity.ModuleDefinition;
import com.example.school.entity.Role;
import com.example.school.entity.SchoolType;
import com.example.school.entity.User;
import com.example.school.repository.AppSettingsRepository;
import com.example.school.repository.CityRepository;
import com.example.school.repository.ModuleDefinitionRepository;
import com.example.school.repository.RoleRepository;
import com.example.school.repository.SchoolTypeRepository;
import com.example.school.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(RoleRepository roleRepo,
                           UserRepository userRepo,
                           SchoolTypeRepository schoolTypeRepo,
                           CityRepository cityRepo,
                           AppSettingsRepository appSettingsRepo,
                           ModuleDefinitionRepository moduleDefinitionRepo,
                           PasswordEncoder encoder,
                           ObjectMapper mapper) {
        return args -> {
            try {
                if (appSettingsRepo.findById(1L).isEmpty()) {
                    AppSettings settings = new AppSettings();
                    settings.setId(1L);
                    settings.setThemePrimary("#1E88E5");
                    appSettingsRepo.save(settings);
                }
                // Create school types
                if (schoolTypeRepo.findByName("Primaire").isEmpty()) {
                    schoolTypeRepo.save(new SchoolType("Primaire"));
                    System.out.println("Type d'etablissement cree : Primaire");
                }
                if (schoolTypeRepo.findByName("Secondaire").isEmpty()) {
                    schoolTypeRepo.save(new SchoolType("Secondaire"));
                    System.out.println("Type d'etablissement cree : Secondaire");
                }
                if (schoolTypeRepo.findByName("Superieur").isEmpty()) {
                    schoolTypeRepo.save(new SchoolType("Superieur"));
                    System.out.println("Type d'etablissement cree : Superieur");
                }
                if (schoolTypeRepo.findByName("Maternelle").isEmpty()) {
                    schoolTypeRepo.save(new SchoolType("Maternelle"));
                    System.out.println("Type d'etablissement cree : Maternelle");
                }
                if (schoolTypeRepo.findByName("Formation Professionnelle").isEmpty()) {
                    schoolTypeRepo.save(new SchoolType("Formation Professionnelle"));
                    System.out.println("Type d'etablissement cree : Formation Professionnelle");
                }

                // Create module catalog (base + premium)
                String[][] baseModules = {
                        {"PROFILE", "Profil etablissement", "Identite, contacts, logo, couleurs"},
                        {"USERS", "Utilisateurs et roles", "Gestion des comptes et droits"},
                        {"PUBLIC_PAGE", "Page publique", "Mini site vitrine editable"},
                        {"ACADEMIC_YEAR", "Année scolaire", "Calendrier et periodes"},
                        {"CLASSES", "Classes et niveaux", "Niveaux, classes et salles"},
                        {"STUDENTS", "Apprenants", "Gestion des apprenants"},
                        {"TEACHERS", "Enseignants", "Gestion des enseignants"},
                        {"SUBJECTS", "Matieres", "Gestion des matieres"},
                        {"CAREER_PATHS", "Parcours - metiers", "Parcours par niveau (matieres, projets, stages)"},
                        {"PEDAGOGICAL_PROJECTS", "Projets pedagogiques", "Gestion des projets pedagogiques"},
                        {"TIMETABLE", "Emploi du temps", "Planning simplifie"},
                        {"COMMUNICATION", "Communication interne", "Annonces et messages"}
                };
                for (String[] m : baseModules) {
                    if (moduleDefinitionRepo.findByCode(m[0]).isEmpty()) {
                        moduleDefinitionRepo.save(new ModuleDefinition(m[0], m[1], m[2], ModuleCategory.BASE));
                    }
                }
                moduleDefinitionRepo.findByCode("STUDENTS").ifPresent(module -> {
                    if (!"Apprenants".equals(module.getName())) {
                        module.setName("Apprenants");
                        module.setDescription("Gestion des apprenants");
                        moduleDefinitionRepo.save(module);
                    }
                });

                String[][] premiumModules = {
                        {"GRADES", "Notes et bulletins", "Evaluations, examens, bulletins"},
                        {"ATTENDANCE", "Presence et absences", "Suivi quotidien des presences"},
                        {"BILLING", "Facturation", "Frais, paiements, relances"},
                        {"PORTAL", "Portail parents/eleves", "Acces web aux resultats"},
                        {"NOTIFICATIONS", "SMS/Email avances", "Notifications et alertes"},
                        {"LIBRARY", "Bibliotheque", "Gestion des ouvrages"},
                        {"TRANSPORT", "Transport et cantine", "Suivi transport/cantine"},
                        {"REPORTS", "Rapports avances", "Statistiques et analyses"},
                        {"HR", "Ressources humaines", "Gestion RH et paie"},
                        {"INSERTION_PRO", "Insertion professionnelle", "Stages et alternances"}
                };
                for (String[] m : premiumModules) {
                    if (moduleDefinitionRepo.findByCode(m[0]).isEmpty()) {
                        moduleDefinitionRepo.save(new ModuleDefinition(m[0], m[1], m[2], ModuleCategory.PREMIUM));
                    }
                }

                // Seed cities (capitals list) if empty
                if (cityRepo.count() == 0) {
                    try (InputStream is = new ClassPathResource("data/cities.json").getInputStream()) {
                        List<Map<String, String>> cities = mapper.readValue(is, new TypeReference<>() {});
                        cities.forEach(c -> {
                            String name = c.get("name");
                            String code = c.get("country");
                            if (name != null && code != null && cityRepo.findByNameAndCountryCode(name, code).isEmpty()) {
                                cityRepo.save(new City(name, code));
                            }
                        });
                        System.out.println("Cities seeded: " + cityRepo.count());
                    } catch (Exception se) {
                        System.err.println("Unable to seed cities: " + se.getMessage());
                    }
                }
                
                // Create roles
                Role superRole = roleRepo.findByName("SUPER_ADMIN").orElseGet(() -> roleRepo.save(new Role("SUPER_ADMIN")));
                Role adminRole = roleRepo.findByName("ADMIN").orElseGet(() -> roleRepo.save(new Role("ADMIN")));
                roleRepo.findByName("USER").orElseGet(() -> roleRepo.save(new Role("USER")));
                roleRepo.findByName("DIRECTION").orElseGet(() -> roleRepo.save(new Role("DIRECTION")));
                roleRepo.findByName("ADHESION").orElseGet(() -> roleRepo.save(new Role("ADHESION")));
                roleRepo.findByName("DIRECTEUR").orElseGet(() -> roleRepo.save(new Role("DIRECTEUR")));
                roleRepo.findByName("SECRETAIRE").orElseGet(() -> roleRepo.save(new Role("SECRETAIRE")));
                roleRepo.findByName("COMPTABLE").orElseGet(() -> roleRepo.save(new Role("COMPTABLE")));
                roleRepo.findByName("DIRECTEUR_RH").orElseGet(() -> roleRepo.save(new Role("DIRECTEUR_RH")));
                roleRepo.findByName("ENSEIGNANT").orElseGet(() -> roleRepo.save(new Role("ENSEIGNANT")));
                roleRepo.findByName("APPRENANT").orElseGet(() -> roleRepo.save(new Role("APPRENANT")));

                Optional<Role> technicienRole = roleRepo.findByName("TECHNICIEN");
                if (technicienRole.isPresent()) {
                    Role tech = technicienRole.get();
                    List<User> techUsers = userRepo.findAll().stream()
                            .filter(u -> u.getRoles().contains(tech))
                            .toList();
                    if (!techUsers.isEmpty()) {
                        for (User u : techUsers) {
                            if (!u.getRoles().contains(adminRole)) {
                                u.getRoles().add(adminRole);
                            }
                            u.getRoles().remove(tech);
                            userRepo.save(u);
                        }
                        System.out.println("Role TECHNICIEN fusionne vers ADMIN pour " + techUsers.size() + " utilisateur(s).");
                    }
                }

                // Create superadmin user
                if (userRepo.findByUsername("superadmin").isEmpty()) {
                    User sa = new User();
                    sa.setUsername("superadmin");
                    sa.setPassword(encoder.encode("admin"));
                    sa.setEnabled(true);
                    HashSet<Role> roles = new HashSet<>();
                    roles.add(superRole);
                    roles.add(adminRole);
                    sa.setRoles(roles);
                    userRepo.save(sa);
                    System.out.println("Superadmin user cree : superadmin / admin");
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de l'initialisation des donnees: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}
