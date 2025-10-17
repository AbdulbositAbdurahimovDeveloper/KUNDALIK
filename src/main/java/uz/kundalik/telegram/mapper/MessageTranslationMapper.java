package uz.kundalik.telegram.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.kundalik.telegram.model.MessageTranslation;
import uz.kundalik.telegram.payload.i18n.MessageTranslationResponseDTO;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface MessageTranslationMapper {
    MessageTranslationResponseDTO toDTO(MessageTranslation updatedTranslation);

}
