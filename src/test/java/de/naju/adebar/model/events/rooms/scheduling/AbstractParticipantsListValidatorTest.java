package de.naju.adebar.model.events.rooms.scheduling;

import de.naju.adebar.TestData;

/**
 * Contains a set of participants to use for testing implementations of
 * {@link ParticipantListValidator}
 *
 * @author Rico Bergmann
 *
 */
public abstract class AbstractParticipantsListValidatorTest {

	/*
   * @formatter:off
   *
   * The participation times look like this:
   *
   * +------+----+----+----+----+----+----+----+----+----+----+----+
   * |      |  1 |  2 |  3 |  4 |  5 |  6 |  7 |  8 |  9 | 10 | 11 |
   * +------+----+----+----+----+----+----+----+----+----+----+----+
   * |hans  | == | == | == |    |    |    |    |    |    |    |    |
   * |martha|    | == | == | == | == |    |    |    |    |    |    |
   * |dieter|    |    |    |    |    | == | == | == |    |    |    |
   * |nadine|    |    |    | == | == | == | == | == | == | == | == |
   * |fritz |    |    |    | == | == |    |    |    | == | == | == |
   * +------+----+----+----+----+----+----+----+----+----+----+----+
   *
   * @formatter:on
   */

	protected Participant hans =
			new Participant(TestData.getParticipant(TestData.PERSON_HANS), new ParticipationTime(1, 3));

	protected Participant martha =
			new Participant(TestData.getParticipant(TestData.PERSON_MARTA), new ParticipationTime(2, 5));

	protected Participant dieter =
			new Participant(TestData.getParticipant(TestData.PERSON_DIETER), new ParticipationTime(6, 8));

	protected Participant nadine = new Participant(TestData.getParticipant(TestData.PERSON_NADINE),
			new ParticipationTime(4, 11));

	protected Participant fritz = new Participant(TestData.getParticipant(TestData.PERSON_FRITZ),
			new ParticipationTime(4, 5), new ParticipationTime(9, 11));

}
