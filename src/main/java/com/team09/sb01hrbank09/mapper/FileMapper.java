package com.team09.sb01hrbank09.mapper;

import org.mapstruct.Mapper;

import com.team09.sb01hrbank09.dto.entityDto.FileDto;
import com.team09.sb01hrbank09.entity.File;

@Mapper(componentModel = "spring")
public interface FileMapper {
	FileDto fileToDto(File file);

	//File dtoToFile(FileDto fileDto);
}
