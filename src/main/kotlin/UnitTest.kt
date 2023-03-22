import org.testng.Assert.assertTrue
import org.testng.annotations.Test

class UnitTest {
    @Test
    fun test1CaoDodgeAttack() {
        heroes.clear()
        monarchHero = CaoCao()
        heroes.add(monarchHero)
        assertTrue(monarchHero.name == "Cao Cao")

        for (i in 1..7) {
            val hero = NoneMonarchFactory.createRandomHero()
            hero.index = heroes.size
            heroes.add(hero)
        }

        if (monarchHero is CaoCao) {
            assertTrue(monarchHero.dodgeAttack())
        }
    }

    @Test
    fun test2BeingAttacked() {
        if (heroes.size == 0) {
            heroes.add(MonarchFactory.createRandomHero() as MonarchHero)
            for (i in 1..7) {
                val hero = NoneMonarchFactory.createRandomHero()
                hero.index = heroes.size
                heroes.add(hero)
            }
        }

        for (hero in heroes) {
            val spy = object : Hero(hero.role) {
                override val name = hero.name
                override fun beingAttacked() {
                    hero.beingAttacked()
                    assertTrue(hero.hp >= 0)
                }
            }
            spy.beingAttacked()
        }
    }

    object FakeMonarchFactory : GameObjectFactory {
        override fun getRandomRole(): Role = MonarchRole()

        override fun createRandomHero(): Hero = CaoCao()
    }

    object FakeNonMonarchFactory : GameObjectFactory {
        private var count = 0
        private var last: WeiHero? = null

        override fun getRandomRole(): Role = MinisterRole()

        override fun createRandomHero(): Hero {
            val hero = when (count++) {
                0 -> SimaYi(getRandomRole())
                1 -> XuChu(getRandomRole())
                2 -> XiaHouyuan(getRandomRole())
                else -> XuChu(getRandomRole())
            }
            val cao = monarchHero as CaoCao
            if (last == null) {
                cao.helper = hero
            } else {
                last!!.setNext(hero)
            }
            last = hero
            return hero
        }
    }

    class CaoCaoUnitTest {
        @Test
        fun test3CaoDodgeAttack() {
            monarchHero = FakeMonarchFactory.createRandomHero() as MonarchHero
            for (i in 1..1) {
                val hero = FakeNonMonarchFactory.createRandomHero()
                hero.index = heroes.size
                heroes.add(hero)
            }

            for (hero in heroes) {
                hero.beingAttacked()
                hero.templateMethod()
                if (hero is CaoCao) {
                    assertTrue(hero.dodgeAttack())
                }
            }
        }
    }

    class DummyRole : Role {
        override val roleTitle: String = "Dummy"
        override fun getEnemy(): String {
            return "For debugging"
        }
    }

    @Test
    fun test4DiscardCards() {
        val dummy = DummyRole()
        val hero = ZhangFei(dummy)
        val spy = object : WarriorHero(MinisterRole()) {
            override val name = hero.name
            override fun discardCards() {
                hero.discardCards()
                assertTrue(hero.numOfCards <= hero.hp)
            }
        }
        for (i in 0..10)
            spy.discardCards()
    }
}
