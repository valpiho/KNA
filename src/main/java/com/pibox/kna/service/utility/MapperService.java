package com.pibox.kna.service.utility;

import com.pibox.kna.domain.Order;
import com.pibox.kna.domain.User;
import com.pibox.kna.service.dto.ClientDTO;
import com.pibox.kna.service.dto.DriverDTO;
import com.pibox.kna.service.dto.OrderResDTO;
import com.pibox.kna.service.dto.UserMiniDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MapperService {

    private final ModelMapper modelMapper;

    public MapperService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public ClientDTO toClientDto(User entity) {
        return modelMapper.map(entity, ClientDTO.class);
    }

    public DriverDTO toDriverDto(User entity) {
        return modelMapper.map(entity, DriverDTO.class);
    }

    public List<UserMiniDTO> convertToListOfUserMiniDTO(List<User> list) {
        return list.stream()
                .map(user -> modelMapper.map(user, UserMiniDTO.class))
                .collect(Collectors.toList());
    }

    public OrderResDTO toOrderResDto(Order order) {
        return modelMapper.map(order, OrderResDTO.class);
    }
}
