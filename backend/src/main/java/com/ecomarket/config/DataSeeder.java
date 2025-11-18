package com.ecomarket.config;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.ecomarket.auth.Role;
import com.ecomarket.auth.RoleRepository;
import com.ecomarket.auth.User;
import com.ecomarket.auth.UserRepository;
import com.ecomarket.category.Category;
import com.ecomarket.category.CategoryRepository;
import com.ecomarket.product.Product;
import com.ecomarket.product.ProductRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(RoleRepository roleRepository, UserRepository userRepository, 
                     CategoryRepository categoryRepository, ProductRepository productRepository,
                     PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Ensure roles exist
        Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseGet(() -> roleRepository.save(new Role(null, "ROLE_ADMIN")));
        Role userRole = roleRepository.findByName("ROLE_USER").orElseGet(() -> roleRepository.save(new Role(null, "ROLE_USER")));

        // Create admin user if not exists
        String adminUsername = "admin";
        if (!userRepository.existsByUsername(adminUsername)) {
            User admin = new User();
            admin.setUsername(adminUsername);
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("AdminPass123"));
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            roles.add(userRole);
            admin.setRoles(roles);
            userRepository.save(admin);
            System.out.println("DataSeeder: created admin user 'admin' with password 'AdminPass123'");
        }

        // Create regular user if not exists
        String userUsername = "user";
        if (!userRepository.existsByUsername(userUsername)) {
            User user = new User();
            user.setUsername(userUsername);
            user.setEmail("user@example.com");
            user.setPassword(passwordEncoder.encode("UserPass123"));
            Set<Role> roles = new HashSet<>();
            roles.add(userRole);
            user.setRoles(roles);
            userRepository.save(user);
            System.out.println("DataSeeder: created regular user 'user' with password 'UserPass123'");
        }

        // Create categories if not exist
        Category furniture = categoryRepository.findByName("Muebles Ecológicos").orElseGet(() -> {
            Category cat = new Category();
            cat.setName("Muebles Ecológicos");
            cat.setDescription("Muebles hechos con materiales reciclados y sostenibles");
            return categoryRepository.save(cat);
        });

        Category accessories = categoryRepository.findByName("Accesorios Sostenibles").orElseGet(() -> {
            Category cat = new Category();
            cat.setName("Accesorios Sostenibles");
            cat.setDescription("Accesorios y complementos ecológicos");
            return categoryRepository.save(cat);
        });

        Category home = categoryRepository.findByName("Hogar Eco-Friendly").orElseGet(() -> {
            Category cat = new Category();
            cat.setName("Hogar Eco-Friendly");
            cat.setDescription("Artículos para el hogar hechos con materiales sostenibles");
            return categoryRepository.save(cat);
        });

        // Create products if not exist
        if (productRepository.count() == 0) {
            Product ecoBackpack = new Product();
            ecoBackpack.setName("Mochila Reciclada EcoTravel");
            ecoBackpack.setDescription("Mochila resistente hecha 100% de botellas plásticas recicladas. Incluye compartimento para laptop y bolsillos organizadores.");
            ecoBackpack.setPrice(new BigDecimal("45.99"));
            ecoBackpack.setStock(35);
            ecoBackpack.setCategory(accessories);
            ecoBackpack.setImageFilename("mochila-reciclada.jpg");
            ecoBackpack.setIsOrganic(false);
            ecoBackpack.setCertifications("Global Recycled Standard (GRS)");
            ecoBackpack.setOriginCountry("Vietnam");
            productRepository.save(ecoBackpack);

            Product recycledTable = new Product();
            recycledTable.setName("Mesa de Madera Recuperada");
            recycledTable.setDescription("Mesa artesanal hecha de madera recuperada de pallets. Acabado natural con sellador ecológico. Perfecta para comedor o escritorio.");
            recycledTable.setPrice(new BigDecimal("189.99"));
            recycledTable.setStock(12);
            recycledTable.setCategory(furniture);
            recycledTable.setImageFilename("mesa-madera-recuperada.jpg");
            recycledTable.setIsOrganic(false);
            recycledTable.setCertifications("FSC Recycled");
            recycledTable.setOriginCountry("México");
            productRepository.save(recycledTable);

            Product ecoBag = new Product();
            ecoBag.setName("Bolsa Reutilizable de Algodón Orgánico");
            ecoBag.setDescription("Set de 3 bolsas de compras hechas de algodón 100% orgánico. Lavables, resistentes y perfectas para el supermercado.");
            ecoBag.setPrice(new BigDecimal("18.50"));
            ecoBag.setStock(120);
            ecoBag.setCategory(accessories);
            ecoBag.setImageFilename("bolsa-algodon-organico.jpg");
            ecoBag.setIsOrganic(true);
            ecoBag.setCertifications("GOTS (Global Organic Textile Standard)");
            ecoBag.setOriginCountry("India");
            productRepository.save(ecoBag);

            Product recycledChair = new Product();
            recycledChair.setName("Silla de Plástico Reciclado Ocean");
            recycledChair.setDescription("Silla moderna hecha de plástico recuperado del océano. Diseño ergonómico, resistente para uso interior y exterior.");
            recycledChair.setPrice(new BigDecimal("95.00"));
            recycledChair.setStock(28);
            recycledChair.setCategory(furniture);
            recycledChair.setImageFilename("silla-plastico-oceano.jpg");
            recycledChair.setIsOrganic(false);
            recycledChair.setCertifications("Ocean Plastic Certified");
            recycledChair.setOriginCountry("Portugal");
            productRepository.save(recycledChair);

            Product bambooCutlery = new Product();
            bambooCutlery.setName("Set de Cubiertos de Bambú");
            bambooCutlery.setDescription("Set portátil de cubiertos reutilizables hechos de bambú sostenible. Incluye tenedor, cuchillo, cuchara, pajita y estuche.");
            bambooCutlery.setPrice(new BigDecimal("12.99"));
            bambooCutlery.setStock(85);
            bambooCutlery.setCategory(home);
            bambooCutlery.setImageFilename("cubiertos-bambu.jpg");
            bambooCutlery.setIsOrganic(false);
            bambooCutlery.setCertifications("FSC Bamboo");
            bambooCutlery.setOriginCountry("China");
            productRepository.save(bambooCutlery);

            Product recycledLamp = new Product();
            recycledLamp.setName("Lámpara de Cartón Reciclado");
            recycledLamp.setDescription("Lámpara de mesa con diseño minimalista hecha de cartón 100% reciclado. Iluminación LED de bajo consumo incluida.");
            recycledLamp.setPrice(new BigDecimal("34.99"));
            recycledLamp.setStock(42);
            recycledLamp.setCategory(home);
            recycledLamp.setImageFilename("lampara-carton.jpg");
            recycledLamp.setIsOrganic(false);
            recycledLamp.setCertifications("Energy Star LED");
            recycledLamp.setOriginCountry("España");
            productRepository.save(recycledLamp);

            Product ecoYogaMat = new Product();
            ecoYogaMat.setName("Tapete de Yoga Cork Natural");
            ecoYogaMat.setDescription("Tapete de yoga hecho de corcho natural y caucho reciclado. Antideslizante, biodegradable y libre de químicos tóxicos.");
            ecoYogaMat.setPrice(new BigDecimal("68.00"));
            ecoYogaMat.setStock(50);
            ecoYogaMat.setCategory(accessories);
            ecoYogaMat.setImageFilename("tapete-yoga-cork.jpg");
            ecoYogaMat.setIsOrganic(false);
            ecoYogaMat.setCertifications("Non-Toxic Certified");
            ecoYogaMat.setOriginCountry("Portugal");
            productRepository.save(ecoYogaMat);

            Product recycledBookshelf = new Product();
            recycledBookshelf.setName("Estante Modular de Metal Reciclado");
            recycledBookshelf.setDescription("Estante industrial de 5 niveles hecho de metal 100% reciclado y madera recuperada. Fácil de ensamblar.");
            recycledBookshelf.setPrice(new BigDecimal("156.00"));
            recycledBookshelf.setStock(18);
            recycledBookshelf.setCategory(furniture);
            recycledBookshelf.setImageFilename("estante-metal-reciclado.jpg");
            recycledBookshelf.setIsOrganic(false);
            recycledBookshelf.setCertifications("Recycled Steel Certified");
            recycledBookshelf.setOriginCountry("México");
            productRepository.save(recycledBookshelf);

            System.out.println("DataSeeder: created sample categories and products");
        }
    }
}
