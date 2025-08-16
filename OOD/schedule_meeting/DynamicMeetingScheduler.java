import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * There is 1 meeting room can host meeting from 9 am to 6 pm Input is in 1 week,
 * I give you what meetings happen on each day (each one will last in an integer hour)
 * Please arrange the meetings for following rules
 * - longer time happening first,
 * - and longer one has longer break time
 * - meeting should begin at the top hour or half hour
 * - First meeting of the day should start at 9 am
 * - If there is more than one meeting, the last meeting should end at 6 pm (The last meeting doesn't need to have a break after it)
 * - if total meeting time longer than 9 hours, then print Unschedulable.
 */
public class DynamicMeetingScheduler {
    // ----- Config -----
    // You can change this to 30, 60, 90, etc. (must be multiple of 30 to honor :00/:30 rule)
    static final int REQUESTED_BREAK_BUDGET_MIN_PER_DAY = 30;

    static class DayInput {
        final String day;
        final List<Integer> durationsHours; // each meeting duration in hours
        DayInput(String day, List<Integer> durationsHours) {
            this.day = day;
            this.durationsHours = durationsHours;
        }
    }

    public static void main(String[] args) {
        // Mock input (your test case)
        List<DayInput> week = List.of(
                new DayInput("Mon", Arrays.asList(1, 3, 3)),
                new DayInput("Tue", Arrays.asList(2, 3)),
                new DayInput("Wed", Arrays.asList(3, 3, 2, 1)),
                new DayInput("Thu", Arrays.asList(4, 2, 1, 1)),
                new DayInput("Fri", Arrays.asList(4, 3, 1, 1, 1))
        );

        for (DayInput di : week) {
            scheduleDay(di.day, new ArrayList<>(di.durationsHours));
        }
    }

    static void scheduleDay(String day, List<Integer> durationsHours) {
        durationsHours.sort((a, b) -> Integer.compare(b, a)); // longest first

        int n = durationsHours.size();
        int totalMeetingMin = durationsHours.stream().mapToInt(h -> h * 60).sum();

        if (totalMeetingMin > 9 * 60) {
            System.out.println(day + ": Unschedulable");
            return;
        }

        int slackForBreaks = 9 * 60 - totalMeetingMin; // must fit in 09:00–18:00

        // NEW RULE:
        // If more than one meeting, force the last meeting to end at 18:00
        // => consume ALL slack as breaks (in 30-min units).
        // If only one meeting, "happens at first" => 0 break.
        int effectiveBreakBudget;
        if (n > 1) {
            effectiveBreakBudget = slackForBreaks;         // fill to 18:00
        } else {
            effectiveBreakBudget = 0;                      // single meeting: no break
        }

        // Round to 30-min granularity to keep :00/:30 alignment
        effectiveBreakBudget -= (effectiveBreakBudget % 30);

        // Safety: if someone passes a bigger requested budget for single-meeting days,
        // we still respect the "single meeting just happen at first" rule by keeping 0.
        // (For multi-meeting days the "end at 18:00" rule takes precedence.)
        int[] breakMins = allocateBreaks(durationsHours, effectiveBreakBudget);

        // Print schedule from 09:00
        System.out.println(day + ":");
        int currentMin = 9 * 60;
        for (int i = 0; i < n; i++) {
            int durMin = durationsHours.get(i) * 60;
            String s = fmt(currentMin);
            String e = fmt(currentMin + durMin);
            System.out.printf("  %s - %s  Meeting (%dh)%n", s, e, durationsHours.get(i));
            currentMin += durMin;

            if (i < n - 1) {
                int b = breakMins[i];
                if (b > 0) {
                    String bs = fmt(currentMin);
                    String be = fmt(currentMin + b);
                    System.out.printf("  %s - %s  Break (%dm)%n", bs, be, b);
                    currentMin += b;
                }
            }
        }
        // If n > 1, currentMin will be exactly 18:00 by construction.
    }

    /**
     * Allocate the total break budget (minutes) across the n-1 breaks, proportionally to the
     * preceding meeting's duration. Uses 30-minute quanta, largest-remainder method, and
     * ensures non-increasing breaks with non-increasing durations (ties allowed).
     */
    static int[] allocateBreaks(List<Integer> durationsHours, int totalBreakBudgetMin) {
        int n = durationsHours.size();
        if (n <= 1 || totalBreakBudgetMin <= 0) return new int[Math.max(0, n - 1)];

        int slots = n - 1; // breaks after each meeting except the last
        int units = totalBreakBudgetMin / REQUESTED_BREAK_BUDGET_MIN_PER_DAY; // work in 30-minute units

        // Weights = hours of the meeting preceding the break slot (index 0..slots-1)
        int[] weights = new int[slots];
        int sumW = 0;
        for (int i = 0; i < slots; i++) {
            weights[i] = Math.max(1, durationsHours.get(i)); // duration as weight (>=1)
            sumW += weights[i];
        }

        // Largest Remainder (Hamilton) method
        double[] ideal = new double[slots];
        int[] base = new int[slots];
        double[] frac = new double[slots];
        int used = 0;
        for (int i = 0; i < slots; i++) {
            ideal[i] = ((double) units) * weights[i] / sumW;
            base[i] = (int) Math.floor(ideal[i]);
            frac[i] = ideal[i] - base[i];
            used += base[i];
        }
        int remaining = units - used;

        // Distribute remaining units by descending fractional part, tie-break by larger weight, then by earlier (longer) meeting
        Integer[] idx = new Integer[slots];
        for (int i = 0; i < slots; i++) idx[i] = i;
        Arrays.sort(idx, (i, j) -> {
            int cmp = Double.compare(frac[j], frac[i]);
            if (cmp != 0) return cmp;
            // tie-breaker: larger weight first
            cmp = Integer.compare(weights[j], weights[i]);
            if (cmp != 0) return cmp;
            // final tie-breaker: earlier index (already longer-or-equal meeting due to sort)
            return Integer.compare(i, j);
        });
        for (int k = 0; k < remaining; k++) base[idx[k]]++;

        // Convert units to minutes
        int[] breaks = new int[slots];
        for (int i = 0; i < slots; i++) breaks[i] = base[i] * 30;
        // Note: total sum remains the same; final break after the last meeting is still 0 by design.
        return breaks;
    }

    static String fmt(int minutes) {
        int h = minutes / 60, m = minutes % 60;
        return String.format("%02d:%02d", h, m);
    }
}
