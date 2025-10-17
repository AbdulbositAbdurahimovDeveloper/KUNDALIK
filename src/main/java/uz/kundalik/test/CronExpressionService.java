package uz.kundalik.test;

import com.cronutils.builder.CronBuilder;
import com.cronutils.model.Cron;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Optional;

import static com.cronutils.model.CronType.UNIX;
import static com.cronutils.model.field.expression.FieldExpressionFactory.*;

@Service
public class CronExpressionService {

    // CronDefinition'ni bir marta yaratib olamiz, bu UNIX formatiga mos (5 qismli)
    private static final CronDefinition CRON_DEFINITION = CronDefinitionBuilder.instanceDefinitionFor(UNIX);
    private static final CronParser CRON_PARSER = new CronParser(CRON_DEFINITION);

    /**
     * cron-utils kutubxonasi yordamida Cron expression yasaydi.
     * Bu usul ancha xavfsiz va qulay.
     */
    public String generateCronExpressionWithLibrary(RecurrenceRequest request) {
        CronBuilder cronBuilder = CronBuilder.cron(CRON_DEFINITION)
                .withMinute(on(request.getMinute()))
                .withHour(on(request.getHour()))
                .withMonth(always()) // Har doim (*)
                .withDoM(always())   // Har doim (*)
                .withDoW(always());  // Har doim (*)

        switch (request.getFrequency()) {
            case WEEKLY:
                // DayOfWeek.getValue() 1-7, cron-utils esa 1-7 qabul qiladi.
                cronBuilder.withDoW(on(request.getDayOfWeek().getValue()));
                break;
            case MONTHLY:
                cronBuilder.withDoM(on(request.getDayOfMonth()));
                break;
        }

        return cronBuilder.instance().asString();
    }


    /**
     * Berilgan cron expression'ni parse qiladi va hozirgi vaqtga mos kelishini tekshiradi.
     * @param cronExpression DB'dan olingan string. Masalan, "30 9 * * 1"
     * @return Hozirgi daqiqaga mos kelsa true, aks holda false.
     */
    public boolean doesCronMatchNow(String cronExpression) {
        try {
            // 1. String'ni Cron obyektiga o'giramiz (parse)
            Cron cron = CRON_PARSER.parse(cronExpression);

            // 2. Tekshirish uchun ExecutionTime yaratamiz
            ExecutionTime executionTime = ExecutionTime.forCron(cron);

            // 3. Hozirgi vaqtni ZonedDateTime'da olamiz (time zone'lar bilan ishlash uchun muhim)
            ZonedDateTime now = ZonedDateTime.now();

            // 4. Mos kelishini tekshiramiz
            return executionTime.isMatch(now);

        } catch (IllegalArgumentException e) {
            // Agar cron expression formati noto'g'ri bo'lsa
            System.err.println("Noto'g'ri cron expression formati: " + cronExpression);
            return false;
        }
    }
}