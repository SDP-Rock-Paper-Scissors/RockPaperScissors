export function test(name: string, fn: () => void) {
  try {
    fn()
    console.log(`✅ ${name}`)
  } catch (e) {
    console.error(`❌ ${name} failed: ${e}`)
  }
}

export function assertTrue(value: boolean) {
  if (!value) {
    throw new Error(`Condition failed`)
  }
}

export function assertEquals(expected: any, actual: any) {
  if (expected !== actual) {
    throw new Error(`expected \'${expected}\', actual \'${actual}\'`)
  }
}