package uz.kundalik.telegram.service.message;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.kundalik.telegram.model.Language;
import uz.kundalik.telegram.payload.i18n.LanguageResponseDTO;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface LanguageMapper {
    LanguageResponseDTO toDTO(Language savedLanguage);
}
