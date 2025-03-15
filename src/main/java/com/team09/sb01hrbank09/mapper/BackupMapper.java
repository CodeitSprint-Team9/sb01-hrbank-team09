package com.team09.sb01hrbank09.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.team09.sb01hrbank09.dto.entityDto.BackupDto;
import com.team09.sb01hrbank09.entity.Backup;

@Mapper(componentModel = "spring")
public interface BackupMapper {

	@Mapping(source = "employeeId.id", target = "employeeId")
	@Mapping(source = "fileId.id", target = "fileId")
	BackupDto backupToDto(Backup backup);

	//Backup dtoToBackup(BackupDto backupDto);

}
