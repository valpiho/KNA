package com.pibox.kna.service;

import com.pibox.kna.domain.User;
import com.pibox.kna.service.dto.UserDTO;
import com.pibox.kna.service.dto.UserMiniDTO;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ModelMapperService {

    private final ModelMapper modelMapper;

    public ModelMapperService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public UserDTO convertToUserDto(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    public List<UserMiniDTO> convertToListOfUserMiniDTO(List<User> list) {
        Converter<?, Boolean> check = ctx -> ctx.getSource() != null;
        modelMapper.typeMap(User.class, UserMiniDTO.class)
                .addMappings(mapper -> mapper.using(check).map(User::getDriver, UserMiniDTO::setDriver))
                .addMappings(mapper -> mapper.using(check).map(User::getClient, UserMiniDTO::setClient));
        return list.stream()
                .map(user -> modelMapper.map(user, UserMiniDTO.class))
                .collect(Collectors.toList());
    }
}
