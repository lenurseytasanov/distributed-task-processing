package edu.java.distributedfileprocessing.mapper;

import edu.java.distributedfileprocessing.domain.Report;
import edu.java.distributedfileprocessing.dto.ReportDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportMapper {

    private final ModelMapper modelMapper;

    public ReportDto toDto(@NonNull Report report) {
        return modelMapper.map(report, ReportDto.class);
    }

    public Report toEntity(@NonNull ReportDto reportDto) {
        return modelMapper.map(reportDto, Report.class);
    }

}
