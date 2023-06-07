package com.schoolmanagement.service;

import com.schoolmanagement.entity.concretes.EducationTerm;
import com.schoolmanagement.entity.concretes.Lesson;
import com.schoolmanagement.entity.concretes.LessonProgram;
import com.schoolmanagement.exception.BadRequestException;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.payload.Response.LessonProgramResponse;
import com.schoolmanagement.payload.Response.ResponseMessage;
import com.schoolmanagement.payload.dto.LessonProgramDto;
import com.schoolmanagement.payload.request.LessonProgramRequest;
import com.schoolmanagement.repository.LessonProgramRepository;
import com.schoolmanagement.utils.Messages;
import com.schoolmanagement.utils.TimeControl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonProgramService {

    private final LessonProgramService lessonProgramService;
    private final LessonService lessonService;
    private final EducationTermService educationTermService;
    private final LessonProgramDto lessonProgramDto;
    private final LessonProgramRepository lessonProgramRepository;
    public ResponseMessage<LessonProgramResponse> save(
            LessonProgramRequest request) {


      Set<Lesson> lessons =
              lessonService.getLessonByLessonIdList(request.getLessonIdList());
      EducationTerm educationTerm =
              educationTermService.getById(request.getEducationTermId());

      //!!!Yukarda geleenn lessons ici bos degilse zaman kontrolu
        if(lessons.size() == 0 ){
            throw new ResourceNotFoundException(Messages.NOT_FOUND_LESSON_IN_LIST);
        }else if(TimeControl.check(request.getStartTime(),request.getStopTime())){
            throw new BadRequestException(Messages.TIME_NOT_VALID_MESSAGE);
        }

        //!!DTO -POJO
        LessonProgram lessonProgram = lessonProgramRequestToDto(request,lessons);
        //!!lessonProgram da education Term bilgisi setleniyor
        lessonProgram.setEducationTerm(educationTerm);
        //lessonProgram DB ye kaydediliyor.
       LessonProgram savedLessonProgram = lessonProgramRepository.save(lessonProgram);
        //ResponMessage objesi olusturuluyor
        return ResponseMessage.<LessonProgramResponse>builder()
                .message("LessonProgram ist Created")
                .httpStatus(HttpStatus.CREATED)
                .object(createLessonProgramResponseForSaveMethod(savedLessonProgram))
                .build();

    }
    private LessonProgram lessonProgramRequestToDto(LessonProgramRequest lessonProgramRequest,Set<Lesson> lessons){
        return lessonProgramDto.dtoLessonProgram(lessonProgramRequest,lessons);
    }

    private LessonProgramResponse createLessonProgramResponseForSaveMethod(LessonProgram lessonProgram){
        return  LessonProgramResponse.builder()
                .day(lessonProgram.getDay())
                .startTime(lessonProgram.getStartTime())
                .stopTime(lessonProgram.getStopTime())
                .lessonProgramId(lessonProgram.getId())
                .lessonName(lessonProgram.getLesson())
                .build();
    }


    //GetAlll
    // Not :  getAll() *************************************************************************
    public List<LessonProgramResponse> getAllLessonProgram() {

        return lessonProgramRepository.findAll()
                .stream()
                .map(this::createLessonProgramResponse)
                .collect(Collectors.toList());

    }
    public LessonProgramResponse createLessonProgramResponse(LessonProgram lessonProgram) {
        return LessonProgramResponse.builder()
                .day(lessonProgram.getDay())
                .startTime(lessonProgram.getStartTime())
                .stopTime(lessonProgram.getStopTime())
                .lessonProgramId(lessonProgram.getId())
                .lessonName(lessonProgram.getLesson())
                //TODO Teacher ve Student yazilinca buraya ekleme yapilacak
                .build();
    }


    public LessonProgramResponse getByLessonProgramId(Long id) {

     LessonProgram lessonProgram =
             lessonProgramRepository.findById(id).orElseThrow(()->{
            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_LESSON_MESSAGE,id));
        });
           return lessonProgramRepository.findById(id)
                   .map(this::createLessonProgramResponse).get();
    }


    public List<LessonProgramResponse> getAllLessonProgramUnassigned() {


        return lessonProgramRepository.findByTeaachers_IdNull()
                .stream()
                .map(this::createLessonProgramResponse)
                .collect(Collectors.toList());
    }


    public List<LessonProgramResponse> getAllLessonProgramAssigned() {

        return lessonProgramRepository.findByTeachers_IdNotNull()
                .stream()
                .map(this::createLessonProgramResponse)
                .collect(Collectors.toList());
    }

    public ResponseMessage deleteLessonProgram(Long id) {
        //!!! id kontrolu
        lessonProgramRepository.findById(id).orElseThrow(()->{
            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_LESSON_MESSAGE,id));
        });
        lessonProgramRepository.deleteById(id);
        //Bu lessonPrograma dahil olan teacer ve student larda degisiklik yapilmasi gerekiyor
        //biz bunu lessonProgram entity sinifi icinde @PreRemove ile yaptik.

        return ResponseMessage.builder()
                .message("Lesson Program is deleted succesfully")
                .httpStatus(HttpStatus.OK)
                .build();
    }


    public Set<LessonProgramResponse> getLessonProgramByTeacher(String username) {
        return lessonProgramRepository.getLessonProgramByTeacherUsername(username)
                .stream()
                .map(this::createLessonProgramResponseForTeacher)
                .collect(Collectors.toSet());
    }
    public LessonProgramResponse createLessonProgramResponseForTeacher(LessonProgram lessonProgram) {
        return LessonProgramResponse.builder()
                .day(lessonProgram.getDay())
                .startTime(lessonProgram.getStartTime())
                .stopTime(lessonProgram.getStopTime())
                .lessonProgramId(lessonProgram.getId())
                .lessonName(lessonProgram.getLesson())
                //TODO Teacher ve Student yazilinca buraya ekleme yapilacak
                .build();
    }










}