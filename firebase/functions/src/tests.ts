import { GameMode } from './index'
import { assertEquals, test } from './test_framework'

test("make sure that it works", function () {
  const code = 'G:rps,MT:PC,P:5,R:3,T:0'
  const mode = new GameMode(code)

  assertEquals(mode.toString(), code)
})