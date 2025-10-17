package uz.kundalik.telegram.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.kundalik.telegram.model.MessageKey;
import uz.kundalik.telegram.payload.i18n.MessageKeyResponseDTO;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface MessageKeyMapper {
    MessageKeyResponseDTO toDTO(MessageKey updatedKey);

}
