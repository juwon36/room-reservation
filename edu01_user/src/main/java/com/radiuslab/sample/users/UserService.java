package com.radiuslab.sample.users;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.radiuslab.sample.users.dto.*;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ModelMapper modelMapper;

	public UserItem save(UserSave userDto) {
		User user = this.modelMapper.map(userDto, User.class);
		User res = this.userRepository.save(user);
		return this.modelMapper.map(res, UserItem.class);
	}

	public UserItem login(UserLogin userDto) {
		User res = this.userRepository.findByEmail(userDto.getEmail());
		if (res == null || !res.getPassword().equals(userDto.getPassword())) {
			return null;
		} else {
			return this.modelMapper.map(res, UserItem.class);
		}
	}

	public UserItem update(UserUpdate userDto) {
		User user = this.modelMapper.map(userDto, User.class);
		User res = this.userRepository.save(user);
		return this.modelMapper.map(res, UserItem.class);
	}

	public List<UserItem> findAll() {
		List<User> res = this.userRepository.findAll();
		return res.stream().map(user -> this.modelMapper.map(user, UserItem.class)).collect(Collectors.toList());
	}

	public void delete(UserItem userDto) {
		User user = this.modelMapper.map(userDto, User.class);
		this.userRepository.delete(user);
	}

	public void deleteAll() {
		this.userRepository.deleteAll();
	}
}
