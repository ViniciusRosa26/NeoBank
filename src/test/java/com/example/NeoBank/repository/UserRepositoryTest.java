package com.example.NeoBank.repository;
import com.example.NeoBank.dto.UserDto;
import com.example.NeoBank.entity.UserEntity;
import com.example.NeoBank.enums.OccupationEnum;
import com.example.NeoBank.enums.TypeAccountEnum;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;



@ActiveProfiles("test")
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;


    @Autowired
    EntityManager entityManager;

    @DisplayName("USUARIO ACHADO PELO EMAIL ")
    @Test
    void findByEmailcase1Sucess() throws ParseException {
        Date dataNascimento = new SimpleDateFormat("dd-MM-yyyy").parse("21-12-1999");
        UserDto userDto= new UserDto ("Fernanda", "TESTE@GMAIL" ,"adm",
            OccupationEnum.AGRICULTOR , "324" ,"823" , 2344.00, TypeAccountEnum.CLT, dataNascimento);
        this.createUser(userDto);
        Optional<UserEntity> foundUser = this.userRepository.findByEmail(userDto.email());
        assertThat(foundUser.isPresent()).isTrue();


    }



    @DisplayName("USUARIO nao ACHADO PELO EMAIL ")
    @Test
    void findByEmailcase2NoSucess() throws ParseException {
        Date dataNascimento = new SimpleDateFormat("dd-MM-yyyy").parse("21-12-1999");
        UserDto userDto= new UserDto ("Fernanda", "TESTE@GMAIL" ,"adm",
                OccupationEnum.AGRICULTOR , "324" ,"823" , 2344.00, TypeAccountEnum.CLT, dataNascimento);
       // this.createUser(userDto);esteja vazio
        Optional<UserEntity> foundUser = this.userRepository.findByEmail(userDto.email());
        assertThat(foundUser.isEmpty()).isTrue();


    }


    private UserEntity createUser(UserDto userDto){


        UserEntity newUser = new UserEntity(userDto);
        this.entityManager.persist(newUser);
        return newUser;
    }
}