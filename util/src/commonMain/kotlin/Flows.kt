package quest.laxla.spock

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.all
import kotlinx.coroutines.flow.any
import kotlinx.coroutines.flow.none

/**
 * A terminal operator that returns `true` if all elements are `true`,
 * or returns `false` and cancels the flow as soon as the first `false` element is encountered.
 *
 * If the flow terminates without emitting any elements, the function returns `true` because there
 * are no elements in it that *do not* match the predicate.
 * See a more detailed explanation of this logic concept in the
 * ["Vacuous truth"](https://en.wikipedia.org/wiki/Vacuous_truth) article.
 *
 * @since 0.0.1-alpha.4
 */
public suspend fun Flow<Boolean>.all(): Boolean = all { it }

/**
 * A terminal operator that returns `true` and immediately cancels the flow
 * if at least one element is `true`.
 *
 * If the flow does not emit any elements, the function returns `false`.
 *
 * @since 0.0.1-alpha.4
 */
public suspend fun Flow<Boolean>.any(): Boolean = any { it }

/** A terminal operator that returns `true` if all elements are `false`,
* or returns `false` and cancels the flow as soon as the `true` element is encountered.
*
* If the flow terminates without emitting any elements, the function returns `true` because there
* are no elements in it that match the predicate.
* See a more detailed explanation of this logic concept in the
* ["Vacuous truth"](https://en.wikipedia.org/wiki/Vacuous_truth) article.
 *
 * @since 0.0.1-alpha.4
*/
public suspend fun Flow<Boolean>.none(): Boolean = none { it }
