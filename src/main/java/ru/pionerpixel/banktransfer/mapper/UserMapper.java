package ru.pionerpixel.banktransfer.mapper;

import org.mapstruct.Mapper;
import ru.pionerpixel.banktransfer.dto.UserDto;
import ru.pionerpixel.banktransfer.model.EmailData;
import ru.pionerpixel.banktransfer.model.PhoneData;
import ru.pionerpixel.banktransfer.model.User;


@Mapper(componentModel = "spring")
public class UserMapper {

    public UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .dateOfBirth(user.getDateOfBirth())
                .phones(user.getPhones().stream().map(PhoneData::getPhone).toList())
                .emails(user.getEmails().stream().map(EmailData::getEmail).toList())
                .balance(user.getAccount().getCurrentBalance())
                .build();
    }
}
