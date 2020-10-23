package test;
class SampleObject{
		Integer attr1;
		Integer attr2;
		String attr3;
		public SampleObject(Integer attr1, Integer attr2, String attr3) {
			super();
			this.attr1 = attr1;
			this.attr2 = attr2;
			this.attr3 = attr3;
		}
		public Integer getAttr1() {
			return attr1;
		}
		public void setAttr1(Integer attr1) {
			this.attr1 = attr1;
		}
		public Integer getAttr2() {
			return attr2;
		}
		public void setAttr2(Integer attr2) {
			this.attr2 = attr2;
		}
		public String getAttr3() {
			return attr3;
		}
		public void setAttr3(String attr3) {
			this.attr3 = attr3;
		}
}